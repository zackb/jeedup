package net.jeedup.entity

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/13/13
 */
@CompileStatic
public interface IEntity {
    public int getEntityTypeId()
    public Object getEntityId()
    public String getTitle()
}