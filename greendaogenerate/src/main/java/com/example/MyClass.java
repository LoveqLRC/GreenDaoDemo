package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyClass {
    public static void main(String args[]){
        //第一个参数版本好，第二参数包名
        Schema schema=new Schema(1,"cn.loveqRc");

        //创建Son表
        Entity son = schema.addEntity("Son");
        son.addStringProperty("name");
        son.addIntProperty("age");
        son.addIdProperty();

        //创建Father表
        Entity father = schema.addEntity("Father");
        father.addStringProperty("name");
        father.addIntProperty("age");
        father.addIdProperty();

        //两个表进行关联,一对一关联
        Property sonId = father.addLongProperty("sonId").getProperty();
//        一个父亲对应一个儿子
        father.addToOne(son,sonId);
//        一个儿子对应多个父亲
        son.addToMany(father,sonId).setName("fathers");
        try {
            //输出到App目录
            new DaoGenerator().generateAll(schema,"app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
