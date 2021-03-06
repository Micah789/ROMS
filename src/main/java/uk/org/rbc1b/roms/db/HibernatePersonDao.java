/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.rbc1b.roms.db;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.org.rbc1b.roms.controller.common.SortDirection;

/**
 * Implements PersonDao.
 *
 * @author rahulsingh
 */
@Repository
public class HibernatePersonDao implements PersonDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Person findPerson(Integer personId) {
        return (Person) this.sessionFactory.getCurrentSession().get(Person.class, personId);
    }

    @Override
    public List<Person> findPersons(String forename, String surname) {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(Person.class)
                .add(Restrictions.eq("forename", forename)).add(Restrictions.eq("surname", surname));

        return criteria.list();
    }

    @Override
    public List<Person> findPersons(PersonSearchCriteria searchCriteria) {
        Session session = this.sessionFactory.getCurrentSession();
        Criteria criteria = createPersonSearchCriteria(searchCriteria, session);

        criteria.setFirstResult(searchCriteria.getStartIndex());
        criteria.setMaxResults(searchCriteria.getMaxResults());

        if (searchCriteria.getSortValue() != null) {
            criteria.addOrder(searchCriteria.getSortDirection() == SortDirection.ASCENDING
                    ? Order.asc(searchCriteria.getSortValue())
                    : Order.desc(searchCriteria.getSortValue()));
        }

        return criteria.list();
    }

    @Override
    public int findPersonsCount(PersonSearchCriteria searchCriteria) {
        Session session = this.sessionFactory.getCurrentSession();
        Criteria criteria = createPersonSearchCriteria(searchCriteria, session);

        criteria.setProjection(Projections.rowCount());

        return ((Long) criteria.uniqueResult()).intValue();
    }

    private Criteria createPersonSearchCriteria(PersonSearchCriteria searchCriteria, Session session) {

        Criteria criteria = session.createCriteria(Person.class);

        if (searchCriteria.getSearch() != null || "congregation.name".equals(searchCriteria.getSortValue())) {
            criteria.createAlias("congregation", "congregation", JoinType.LEFT_OUTER_JOIN);
        }

        if (searchCriteria.getSearch() != null) {
            String searchValue = "%" + searchCriteria.getSearch() + "%";

            criteria.add(Restrictions.or(Restrictions.like("forename", searchValue),
                    Restrictions.like("middleName", searchValue),
                    Restrictions.like("surname", searchValue),
                    Restrictions.like("email", searchValue),
                    Restrictions.like("congregation.name", searchValue)));
        }

        return criteria;
    }

    @Override
    public void savePerson(Person person) {
        if (person.getPersonId() != null) {
            this.sessionFactory.getCurrentSession().merge(person);
        } else {
            this.sessionFactory.getCurrentSession().save(person);
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
