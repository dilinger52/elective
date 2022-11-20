package org.elective.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsSubtopic;
import org.elective.database.entity.Subtopic;
import org.elective.database.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.StudentsSubtopicManager.findStudentsSubtopic;
import static org.elective.logic.SubtopicManager.findSubtopicsByCourse;

public class Filter {

    private static final Logger logger = LogManager.getLogger(Filter.class);

    private Filter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Filter topic filters list of courses that will be displayed by specified topics.
     *
     * @param topics  array of topics for which filtering courses
     * @param courses list of courses to filter
     * @return the list of courses after filtering
     */
    public static List<Course> filterTopic(String[] topics, List<Course> courses) {
        if (topics != null) {
            List<Course> result = new ArrayList<>();
            for (String topic : topics) {
                courses.stream()
                        .filter(c -> c.getTopic().equals(topic))
                        .forEach(result::add);
            }
            return result;
        }
        return courses;
    }

    /**
     * Filter teacher filters list of courses that will be displayed by specified teachers.
     *
     * @param teachers array of teachers for which filtering courses
     * @param courses  list of courses to filter
     * @return the list of courses after filtering
     */
    public static List<Course> filterTeachers(String[] teachers, List<Course> courses) {
        if (teachers != null) {
            List<Course> result = new ArrayList<>();
            for (String teacher : teachers) {
                courses.stream()
                        .filter(c -> c.getTeacherId() == Integer.parseInt(teacher))
                        .forEach(result::add);
            }
            return result;
        }
        return courses;
    }

    /**
     * Filter completions filters list of courses that will be displayed by specified completion.
     *
     * @param completions the completions of course by current student
     * @param courses     the courses of current student
     * @param user        current student
     * @return the list of courses after filtering
     * @throws DBException custom exception that signals database errors
     */
    public static List<Course> filterCompletions(String[] completions, List<Course> courses, User user) throws DBException {
        if (completions != null) {
            try {
                List<Course> result = new ArrayList<>();
                for (Course course : courses) {
                    List<Subtopic> subtopics = findSubtopicsByCourse(course.getId());

                    List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();
                    for (Subtopic subtopic : subtopics) {
                        studentsSubtopics.add(findStudentsSubtopic(subtopic.getId(), user.getId()));
                    }

                    List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                            .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                            .collect(Collectors.toList());

                    for (String completion : completions) {
                        checkCompletion(result, course, studentsSubtopics, finishedSubtopics, completion);
                    }

                }
                return result;
            } catch (Exception e) {
                logger.error(e);
                throw new DBException("CannotFilterByCompletions", e);
            }
        }
        return courses;

    }

    private static void checkCompletion(List<Course> result, Course course, List<StudentsSubtopic> studentsSubtopics, List<StudentsSubtopic> finishedSubtopics, String completion) {
        if (completion.equals("0") && finishedSubtopics.isEmpty()) {
            result.add(course);
        }
        if (completion.equals("2") && finishedSubtopics.size() == studentsSubtopics.size() && !studentsSubtopics.isEmpty()) {
            result.add(course);
        }
        if (completion.equals("1") && !finishedSubtopics.isEmpty() && finishedSubtopics.size() < studentsSubtopics.size()) {
            result.add(course);
        }
    }

    public static List<Course> filterName(String pattern, List<Course> courses) {
        return courses.stream()
                .filter(course -> course.getName().toLowerCase().matches(".*" + pattern.toLowerCase() + ".*"))
                .collect(Collectors.toList());
    }

}
