package org.elective.utils;

import org.elective.database.entity.Course;

import java.util.*;

/**
 * Utility class that contains method to create HashMap that realized pagination mechanism
 */
public class Pagination {

    private Pagination() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Takes list of courses and returns map in which courses are divided into pages in a given number
     *
     * @param courses list of courses that need to be converted
     * @return the HashMap that key is number of the page, and value is list of courses that would be displayed at relevant page
     */
    public static Map<Integer, List<Course>> pageConstructor(List<Course> courses) {
        List<Course> c = new ArrayList<>();
        Map<Integer, List<Course>> pages = new HashMap<>();
        ListIterator<Course> it = courses.listIterator();
        int count = 0;
        int pageNumber = 0;
        while (it.hasNext()) {
            if (count < 3) {
                c.add(it.next());
                count++;
            } else {
                pages.put(pageNumber, c);
                pageNumber++;
                c = new ArrayList<>();
                c.add(it.next());
                count = 1;
            }

        }
        pages.put(pageNumber, c);
        return pages;
    }
}
