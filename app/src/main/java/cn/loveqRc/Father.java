package cn.loveqRc;

import cn.loveqRc.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FATHER".
 */
public class Father {

    private String name;
    private Integer age;
    private Long id;
    private Long sonId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient FatherDao myDao;

    private Son son;
    private Long son__resolvedKey;


    public Father() {
    }

    public Father(Long id) {
        this.id = id;
    }

    public Father(String name, Integer age, Long id, Long sonId) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.sonId = sonId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFatherDao() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSonId() {
        return sonId;
    }

    public void setSonId(Long sonId) {
        this.sonId = sonId;
    }

    /** To-one relationship, resolved on first access. */
    public Son getSon() {
        Long __key = this.sonId;
        if (son__resolvedKey == null || !son__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SonDao targetDao = daoSession.getSonDao();
            Son sonNew = targetDao.load(__key);
            synchronized (this) {
                son = sonNew;
            	son__resolvedKey = __key;
            }
        }
        return son;
    }

    public void setSon(Son son) {
        synchronized (this) {
            this.son = son;
            sonId = son == null ? null : son.getId();
            son__resolvedKey = sonId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}