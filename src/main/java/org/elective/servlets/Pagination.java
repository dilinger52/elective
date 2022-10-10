package org.elective.servlets;

import org.elective.DBManager.entity.Course;

import java.util.*;

public class Pagination {

    private Pagination() {
        throw new IllegalStateException("Utility class");
    }
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
