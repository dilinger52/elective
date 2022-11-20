package org.elective.utils;

import org.elective.database.DBException;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;
import org.elective.logic.SubtopicManager;

import javax.servlet.http.HttpServletRequest;
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

    public static Map<Integer, Subtopic> getSubtopicPages(int courseId) throws DBException {
        Map<Integer, Subtopic> result = new HashMap<>();
        List<Subtopic> subtopics = SubtopicManager.findSubtopicsByCourse(courseId);
        for (int i = 0; i < subtopics.size(); i++) {
            result.put(i, subtopics.get(i));
        }
        return result;
    }

    public static int getNewPageKey(HttpServletRequest req, Map<Integer, Subtopic> pages) {
        int newPageKey = Integer.parseInt(req.getParameter("new_page_key"));
        if (newPageKey < 0) {
            newPageKey = 0;
        } else if (newPageKey >= pages.size()) {
            newPageKey = pages.size() - 1;
        }
        return newPageKey;
    }

}
