/*
 * Copyright (C) 2021 Eugen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package labs.pm.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Eugen
 */
public class ProductManager {

//    private Product product;
//    private Review[] reviews = new Review[5];
    private Map<Product, List<Review>> products = new HashMap<>();
//    private ResourceFormatter formatter;
    private static final Map<String, ResourceFormatter> formatters
            = Map.of("en-GB", new ResourceFormatter(Locale.UK),
                    "en-US", new ResourceFormatter(Locale.US),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "zh-CN", new ResourceFormatter(Locale.CHINA));
    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());
    private final ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private final Path reportsFolder
            = Path.of(config.getString("reports.folder"));
    private final Path dataFolder
            = Path.of(config.getString("data.folder"));
    private final Path tempFolder
            = Path.of(config.getString("temp.folder"));
    private static final ProductManager pm = new ProductManager();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
//    public ProductManager(Locale locale) {
//        this(locale.toLanguageTag());
//    }

    private ProductManager() {
        //       changeLocale(languageTag);
        loadAllData();
    }

//    public void changeLocale(String languageTag) {
//        formatter = formatters.getOrDefault(languageTag,
//                formatters.get("en-GB"));
//    }
    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Food(id, name, price, rating, bestBefore);
            products.putIfAbsent(product, new ArrayList<>());
            return product;
        } catch (Exception ex) {
            logger.log(Level.INFO, "Error adding product " + ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Drink(id, name, price, rating);
            products.putIfAbsent(product, new ArrayList<>());
            return product;
        } catch (Exception ex) {
            logger.log(Level.INFO, "Error adding product " + ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }

    }

    public Product reviewProduct(int id, Rating rating, String comment) {
        try {
            writeLock.lock();
            return reviewProduct(findProduct(id), rating, comment);

        } catch (ProductManagerException ex) {
            logger.log(Level.INFO, ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }

    }

    private Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));

        product = product.applyRating(Rateable.convert((int) Math.round(reviews
                .stream()
                .mapToInt(p -> p.getRating().ordinal())
                .average()
                .orElse(0))));

        products.put(product, reviews);
        return product;
    }

    public void printProductReport(int id, String languageTag, String client) {
        try {
            readLock.lock();
            printProductReport(findProduct(id), languageTag, client);
        } catch (ProductManagerException ex) {
            logger.log(Level.INFO, ex.getMessage());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "error printing product report " + ex.getMessage(), ex);
        } finally {
            readLock.unlock();
        }
    }

    public void printProductReport(Product product, String languageTag, String client) throws IOException {
        ResourceFormatter formatter = formatters.getOrDefault(languageTag,
                formatters.get("en-GB"));
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
//        StringBuilder txt = new StringBuilder();
        Path productReportFile
                = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productReportFile, StandardOpenOption.CREATE), "UTF-8"))) {

            out.append(formatter.formatProduct(product) + System.lineSeparator());

//        for (Review review : reviews) {
//            txt.append(formatter.formatReview(review));
//            txt.append("\n");
//        }
            if (reviews.isEmpty()) {
                out.append(formatter.getText("no.reviews") + System.lineSeparator());

            } else {
                out.append(reviews.stream()
                        .map(r -> formatter.formatReview(r) + System.lineSeparator())
                        .collect(Collectors.joining()));
            }
            //  System.out.println(txt);
        }
    }

    private void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag,
                    formatters.get("en-GB"));
            StringBuilder txt = new StringBuilder();

            products.keySet()
                    .stream()
                    .sorted(sorter)
                    .filter(filter)
                    .forEach(p -> txt.append(formatter.formatProduct(p) + '\n'));
            System.out.println(txt);
        } finally {
            readLock.unlock();
        }
    }

    private void printProductsIterative(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
        ResourceFormatter formatter = formatters.getOrDefault(languageTag,
                formatters.get("en-GB"));
        List<Product> productList = new ArrayList<>(products.keySet());
        productList.sort(sorter);
        StringBuilder txt = new StringBuilder();
        for (Product product : productList) {
            txt.append(formatter.formatProduct(product));
            txt.append('\n');
        }

        System.out.println(txt);

    }

    public Product findProduct(int id) throws ProductManagerException {
        try {
            readLock.lock();
            return products.keySet()
                    .stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product with id "
                    + id + " not found"));
        } finally {
            readLock.unlock();
        }
    }

    public Product findProductIterative(int id) throws ProductManagerException {
        Product result = null;
        for (Product product : products.keySet()) {
            if (product.getId() == id) {
                result = product;
                break;
            }
        }
        return result;
    }

    public Map<String, String> getDiscounts(String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag,
                    formatters.get("en-GB"));
            return products.keySet()
                    .stream()
                    .collect(
                            Collectors.groupingBy(
                                    product -> product.getRating().getStars(),
                                    Collectors.collectingAndThen(
                                            Collectors.summingDouble(
                                                    product -> product.getDiscount().doubleValue()),
                                            discount -> formatter.moneyFormat.format(discount))));
        } finally {
            readLock.unlock();
        }
    }

    private static class ResourceFormatter {

        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            this.resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            this.dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                    .localizedBy(locale);
            this.moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(resources.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));
        }

        private String formatReview(Review review) {
            return MessageFormat.format(resources.getString("review"),
                    review.getRating().getStars(),
                    review.getComments());
        }

        private String getText(String key) {
            return resources.getString(key);
        }
    }

    public Product loadProduct(Path file) {
        Product product = null;
        try {
            product = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8"))
                    .findFirst().orElseThrow());
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error loading product " + ex.getMessage());
        }

        return product;
    }

    public List<Review> loadReviews(Product product) {
        List<Review> reviews = null;
        Path file = reportsFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try {
                Files.lines(file, Charset.forName("UTF-8"))
                        .map(text -> parseReview(text))
                        .filter(review -> review != null)
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Error loading reviews " + ex.getMessage());
            }
        }
        return reviews;
    }

    private void loadAllData() {
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(file -> loadProduct(file))
                    .filter(product -> product != null)
                    .collect(Collectors.toMap(
                            product -> product, product -> loadReviews(product)));

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error loading all data " + ex.getMessage(), ex);
        }

    }

    @SuppressWarnings("unchecked")
    private void restoreData() {
        try {
            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith("tmp"))
                    .findFirst()
                    .orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(
                    Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
                products = (HashMap) in.readObject();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error restoring tmp data " + ex.getMessage(), ex);
        }
    }

    private void dumpData() {
        try {
            if (Files.notExists(tempFolder)) {
                Files.createDirectory(tempFolder);
            }
            Path tempFile = tempFolder.resolve(
                    MessageFormat.format(config.getString("temp.file"), Instant.now().toString().substring(0, 13)));
            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
                out.writeObject(products);
//                products = new HashMap<>();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error dumping data " + ex.getMessage(), ex);
        }
    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);
            //reviewProduct(Integer.parseInt((String) values[0]), Rateable.convert(Integer.parseInt((String) values[1])), (String) values[2]);
        } catch (ParseException | NumberFormatException ex) {
            logger.log(Level.WARNING, "Error parsing review " + text);
        }
        return review;
    }

    private Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            int id = Integer.parseInt((String) values[1]);
            String name = ((String) values[2]);
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(((String) values[3])));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch ((String) values[0]) {
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    product = new Food(id, name, price, rating, bestBefore);
                    break;
            }
        } catch (ParseException | NumberFormatException | DateTimeException ex) {
            logger.log(Level.WARNING, "Error parsing product " + text);
        }
        return product;
    }

    public static ProductManager getInstance() {
        return pm;
    }

    //     private Product reviewProductIterative(Product product, Rating rating, String comments) {
//         List<Review> reviews = product.getRating();
//        if (reviews[reviews.length - 1] != null) {
//            reviews = Arrays.copyOf(reviews, reviews.length + 5);
//        }
//        int sum = 0, i = 0;
//        boolean reviewed = false;
//        while (i < reviews.length && !reviewed) {
//            if (reviews[i] == null) {
//                reviews[i] = new Review(rating, comments);
//                reviewed = true;
//            }
//            sum += reviews[i].getRating().ordinal();
//            i++;
//        }
//     }
}
