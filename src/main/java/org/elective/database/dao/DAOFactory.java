package org.elective.database.dao;

public abstract class DAOFactory {

    private static DAOFactory instance;
    private static String DAOFactoryFCN;

    protected DAOFactory() {
        //nothing to do
    }

    public static synchronized DAOFactory getInstance() throws Exception {
        if (instance == null) {
            Class<?> clazz = Class.forName(DAOFactory.DAOFactoryFCN);
            instance = (DAOFactory) clazz.getDeclaredConstructor().newInstance();
        }
        return instance;
    }

    public static void setDAOFactoryFCN(String daoFactoryFCN) {
        instance = null;
        DAOFactory.DAOFactoryFCN = daoFactoryFCN;
    }

    public abstract RoleDAO getRoleDAO();

    public abstract UserDAO getUserDAO();

    public abstract CourseDAO getCourseDAO();

    public abstract SubtopicDAO getSubtopicDAO();

    public abstract StudentsSubtopicDAO getStudentsSubtopicDAO();

    public abstract StudentsCourseDAO getStudentsCourseDAO();
}
