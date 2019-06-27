package br.gov.caixa.gitecsa.arquitetura.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

public abstract class AbstractBaseDAOImpl {

	@PersistenceContext(unitName="sicrvPU")
	private EntityManager em;
	
    protected Session getSession() {
		return em.unwrap(Session.class);
	}

    public void setEntityManager(final EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

}
