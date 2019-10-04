Hibernate的查询方式
hibernate的查询方式常见的主要分为三种: HQL, QBC（命名查询）, 以及使用原生SQL查询（SqlQuery）

一、HQL查询

• HQL（Hibernate Query Language）提供了丰富灵活的查询方式，使用HQL进行查询也是Hibernate官方推荐使用的查询方式。

• HQL在语法结构上和SQL语句十分的相同，所以可以很快的上手进行使用。使用HQL需要用到Hibernate中的Query对象，该对象专门执行HQL方式的操作。
查询所有示例

```java
session.beginTransaction();
String hql = "from User"; // from 后跟的是要查询的对象，而不是表
Query query = session.createQuery(hql);
List<User> userList = query.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();
```

带where的查询示例

```java
session.beginTransaction();
String hql = "from User where userName = 'James'";
Query query = session.createQuery(hql);
List<User> userList = query.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();
/*
在HQL中where语句中使用的是持久化对象的属性名，如上面示例中的userName。当然在HQL中也可以使用别名
*/
String hql = "from User as u where u.userName = 'James'";
/*
过滤条件
在where语句中还可以使用各种过滤条件，如：=、<>、<、>、>=、<=、between、not between、
in、not in、is、like、and、or等

```

获取一个不完整的对象

```java

session.beginTransaction();
String hql = "select userName from User";
Query query = session.createQuery(hql);
List<Object> nameList = query.list();
for(Object obj:nameList){
　　 String name=(String)obj;
　　 System.out.println(name);
}
session.getTransaction().commit();
// 多个属性的话，需要用object[]接收
session.beginTransaction();
String hql = "select userName,userPwd from User";
Query query = session.createQuery(hql);
List nameList = query.list();
for(Object obj:nameList){
Object[] array = (Object[]) obj; // 转成object[]
System.out.println("name:" + array[0]);
System.out.println("pwd:" + array[1]);
}
session.getTransaction().commit();

```

统计和分组查询

```java
session.beginTransaction();
String hql = "select count(*),max(id) from User";
Query query = session.createQuery(hql);
List nameList = query.list();
for(Object obj:nameList){
Object[] array = (Object[]) obj;
System.out.println("count:" + array[0]);
System.out.println("max:" + array[1]);
}
session.getTransaction().commit();
/*
该条sql语句返回的是单条数据，所以还可以这样写
单列数据用Object，多列数据用Object[]
*/
Object[] object = (Object[]) query.uniqueResult();
System.out.println("count:" + object[0]);
System.out.println("max:" + object[1]);
```

HQL占位符

```java
session.beginTransaction();
String hql = "from User where userName = ?";
Query query = session.createQuery(hql);
// 索引从0开始
query.setString(0, "James");
List<User> userList = query.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();

```

HQL引用占位符

```java
session.beginTransaction();
String hql = "from User where userName = :name";
Query query = session.createQuery(hql);
query.setParameter("name", "James");
List<User> userList = query.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();
```

HQL分页

```java

session.beginTransaction();
String hql = "from User";
Query query = session.createQuery(hql);
query.setFirstResult(0);//起始页码
query.setMaxResults(2);//终止页码
List<User> userList = query.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();

```

二、QBC（Query By Criteria）查询

• Criteria对象提供了一种面向对象的方式查询数据库。Criteria对象需要使用Session对象来获得。

• 一个Criteria对象表示对一个持久化类的查询。

查询所有

```java
session.beginTransaction();
Criteria c = session.createCriteria(User.class);
List<User> userList = c.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();
```

where

```java
session.beginTransaction();
Criteria c = session.createCriteria(User.class);
c.add(Restrictions.eq("userName", "James"));
List<User> userList = c.list();
for(User user:userList){
System.out.println(user.getUserName());
}
session.getTransaction().commit();

```

Restrictions对象

```java
方法名称 对应SQL中的表达式
----------------------------------------------------------
Restrictions.eq field = value
Restrictions.gt field > value
Restrictions.lt field < value
Restrictions.ge field >= value
Restrictions.le field <= value
Restrictions.between field between value1 and value2
Restrictions.in field in(…)
Restrictions.and and
Restrictions.or or
Restrictions.like field like value

```

示例

```java

Criteria c = session.createCriteria(User.class);
c.add(Restrictions.like("userName", "J"));
c.add(Restrictions.eq("id", 120));
c.add(Restrictions.or(Restrictions.eq("userName", "James"),
Restrictions.eq("userName", "Alex")));
```

获取唯一记录

```java

session.beginTransaction();
Criteria c = session.createCriteria(User.class);
c.add(Restrictions.eq("id", 120));
User user = (User) c.uniqueResult();
System.out.println(user.getUserName());
session.getTransaction().commit();
```

分页

```java
Criteria c = session.createCriteria(User.class);
c.setFirstResult(0);
c.setMaxResults(5);
```

分组与统计

```java
session.beginTransaction();
Criteria c = session.createCriteria(User.class);
c.setProjection(Projections.sum("id"));
Object obj = c.uniqueResult();
System.out.println(obj);
session.getTransaction().commit();
```

Projections对象

```java
方法名称 描述
-------------------------------------------------------
Projections.sum 等于SQL中聚合函数sum
Projections.avg 等于SQL中聚合函数avg
Projections.count 等于SQL中聚合函数count
Projections .distinct 去除重复记录
Projections.max 等于SQL中聚合函数max
Projections.min 等于SQL中聚合函数min
Projections .groupProperty 对指定的属性进行分组查询
```

多个统计与分组

```java
session.beginTransaction();
Criteria c = session.createCriteria(User.class);
ProjectionList projectionList = Projections.projectionList();
projectionList.add(Projections.sum("id"));
projectionList.add(Projections.min("id"));
c.setProjection(projectionList);
// 和HQL一样，单列用Object，多列用Object[]
Object[] obj = (Object[]) c.uniqueResult();
System.out.println("sum:" + obj[0]);
System.out.println("min:" + obj[1]);

```

排序

```java
Criteria c = session.createCriteria(User.class);
c.addOrder(Order.desc("id"));
```

三、原生SQL查询：

　　　　　　　　　　　　　　　　　　　　　　　示例

```java
session.beginTransaction();
String sql = "select id,username,userpwd from t_user";
List list = session.createSQLQuery(sql).list();
for(Object item : list){
Object[] rows = (Object[]) item;
System.out.println("id:" + rows[0] + "username:"
+ rows[1] + "userpwd:" + rows[2]);
}
session.getTransaction().commit();
```

addEntity()示例

```java

session.beginTransaction();
String sql = "select id,username,userpwd from t_user";
// addEntity()可以告诉Hibernate你想要封装成对象的类型，然后自动为你封装
SQLQuery query = session.createSQLQuery(sql).addEntity(User.class);
List<User> list = query.list();
for(User user : list){
System.out.println(user.getUserName());
}
session.getTransaction().commit();

```

uniqueResult示例

```java

session.beginTransaction();
String sql = "select id,username,userpwd from t_user where id = 2";
SQLQuery query = session.createSQLQuery(sql).addEntity(User.class);
User user = (User) query.uniqueResult();
System.out.println(user.getUserName());
session.getTransaction().commit();
```

