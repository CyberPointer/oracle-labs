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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

/**
 *
 * @author Eugen
 */
public class CommentedShopLines {
    public void commentedLines(){
        ProductManager pm = ProductManager.getInstance();
//         pm.printProductReport(101);
//
//        pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99),
//                Rating.NOT_RATED);
//        pm.parseProduct("D,101,Tea,1.99,0,2019-09-19");
//        pm.printProductReport(101);
//
//        pm.reviewProduct(101, Rating.FOUR_STAR, "Nice hot cup of tea");
//        pm.reviewProduct(101, Rating.TWO_STAR, "Rather weak tea");
//        pm.reviewProduct(101, Rating.FOUR_STAR, "Fine tea");
//        pm.reviewProduct(101, Rating.FOUR_STAR, "Good tea");
//        pm.reviewProduct(101, Rating.FIVE_STAR, "Perfect tea");
//        pm.reviewProduct(101, Rating.THREE_STAR, "Just add some lemon");
//        pm.parseReview("101,4,Nice hot cup of tea");
//        pm.parseReview("101,2,Rather weak tea");
//        pm.parseReview("101,4,Fine tea");
//        pm.parseReview("101,4,Good tea");
//        pm.parseReview("101,5,Perfect tea");
//        pm.printProductReport(101);
//        pm.parseProduct("F,103,Cacke,3.99,0,2019-09-19");
//        pm.createProduct(164, "Kombucha", BigDecimal.valueOf(1.99),
//                Rating.NOT_RATED);
//        pm.reviewProduct(164, Rating.TWO_STAR, "Look like tea but is it?");
//        pm.reviewProduct(164, Rating.FOUR_STAR, "Fine tea!");
//        pm.reviewProduct(164, Rating.FOUR_STAR, "This is not tea");
//        pm.reviewProduct(164, Rating.FIVE_STAR, "Perfect");
//
//        pm.printProductReport(103);
//        pm.printProductReport(164);
//
//
//        pm.createProduct(102, "Cofe", BigDecimal.valueOf(1.99),
//                Rating.NOT_RATED);
//        pm.reviewProduct(102, Rating.FOUR_STAR, "Coffee was ok");
//        pm.reviewProduct(102, Rating.TWO_STAR, "Where is the milk?!");
//        pm.reviewProduct(102, Rating.FOUR_STAR, "It's perfect");
//         pm.printProductReport(102);
//
//        Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99),
//                Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        pm.reviewProduct(103, Rating.FOUR_STAR, "Very nice cacke");
//        pm.reviewProduct(103, Rating.FOUR_STAR, "It good, but "
//                + "I've expected more choclate");
//        pm.reviewProduct(103, Rating.FIVE_STAR, "This cake is perfect");
//        pm.printProductReport(103);
//
//        Product p4 = pm.createProduct(104, "Cookie", BigDecimal.valueOf(3.99),
//                Rating.TWO_STAR, LocalDate.now());
//        pm.reviewProduct(104, Rating.THREE_STAR, "Just another cookie");
//        pm.reviewProduct(104, Rating.THREE_STAR, "Ok");
//          pm.printProductReport(104);
//
//        Product p5 = pm.createProduct(105, "Chocolate", BigDecimal.valueOf(2.99),
//                Rating.FIVE_STAR);
//        pm.reviewProduct(105, Rating.FOUR_STAR, "Tasty!");
//        pm.reviewProduct(105, Rating.FOUR_STAR, "Not bad at all");
//         pm.printProductReport(105);
//
//        Product p6 = pm.createProduct(106, "Chocolate", BigDecimal.valueOf(2.99),
//                Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        pm.reviewProduct(106, Rating.TWO_STAR, "Too sweet");
//        pm.reviewProduct(106, Rating.THREE_STAR, "Betther than coockie");
//        pm.reviewProduct(106, Rating.TWO_STAR, "Too bitter");
//        pm.reviewProduct(106, Rating.ONE_STAR, "I don't get it!");
//        pm.printProductReport(106);
//        pm.printProducts((p -> p.getPrice().floatValue() < 2),
//                (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
//          pm.printProducts((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
//
//        pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + '\t' + discount));
//        Comparator<Product> ratingSorter = ((p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
//        Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
//
//          pm.printProducts(ratingSorter.thenComparing(priceSorter));
//          pm.printProducts(priceSorter);
//         pm.printProducts((p1, p2) -> p2.getDiscount().compareTo(p1.getPrice()));
    }
}
