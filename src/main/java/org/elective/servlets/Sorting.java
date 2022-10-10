package org.elective.servlets;

import org.elective.DBManager.entity.Course;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sorting {

    private Sorting() {
        throw new IllegalStateException("Utility class");
    }
    public static List<Course> sort(HttpSession session, List<Course> courses, int pattern) {
        switch (pattern){
            case 1: courses = courses.stream().sorted((o1, o2) -> {
                Map<Integer, Integer> coursesStudents = (Map<Integer, Integer>) session.getAttribute("coursesStudents");
                return coursesStudents.get(o1.getId()).compareTo(coursesStudents.get(o2.getId()));
            }).collect(Collectors.toList());
                break;
            case 2: courses = courses.stream().sorted((o1, o2) -> {
                Map<Integer, Integer> coursesStudents = (Map<Integer, Integer>) session.getAttribute("coursesStudents");
                return -1 * (coursesStudents.get(o1.getId()).compareTo(coursesStudents.get(o2.getId())));
            }).collect(Collectors.toList());
                break;
            default:
            case 0:
            case 3: courses = courses.stream()
                    .sorted((o1, o2) -> (o1.getName().compareToIgnoreCase(o2.getName())))
                    .collect(Collectors.toList());
                break;
            case 4: courses = courses.stream()
                    .sorted((o1, o2) -> -1 * (o1.getName().compareToIgnoreCase(o2.getName())))
                    .collect(Collectors.toList());
                break;
            case 5:courses = courses.stream()
                    .sorted(Comparator.comparing(Course::getDuration))
                    .collect(Collectors.toList());
                break;
            case 6:courses = courses.stream()
                    .sorted((o1, o2) -> -1 * (String.valueOf(o1.getDuration()).compareTo(String.valueOf(o2.getDuration()))))
                    .collect(Collectors.toList());
                break;
        }
        return courses;
    }
}
