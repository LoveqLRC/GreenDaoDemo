#GreenDao

 - Android原生提供的Api给我们带来哪些不方便？
	 1.要手动拼装sql
	 2.要自己写常规数据库操作代码
	 3.不能自动把数据库中的数据影射成对象
	 4.没有实现级联查询
	 
 - [什么是GreenDao](http://greenrobot.org/greendao/)？
 - 什么是ORM？优点？
	 1. 对象关系映射（英语：Object Relation Mapping，简称ORM，或O/RM，或O/R mapping），是一种程序技术，用于实现面向对象编程语言里不同类型系统的数据之间的转换
	 2. 优点
			 1. 让业务代码访问对象，而不是数据库
			 2. 隐藏了面向对象的逻辑SQL查询详情
			 3. 无需处理数据库的实现
	 
	 
 - 主流Orm框架？
	 1. OrmLite
	 2. LitePal
	 3. GreenDao等
	 
 - 为什么选择GreenDao?
	 1. 性能
	 2. 文档
	 3. 流行性
	 4. 使用是否Easy
	 5. 拓展性如何
	 
 - 学习方法:[GreenDaoGithub](https://github.com/greenrobot/greenDAO)

	 
##Android Studio配置GreenDao

 1. `App Module` 添加`  compile 'de.greenrobot:greendao:2.1.0'`
 2. 在工程目下，new ->Module新建`java library` 在`build.gradle` 添加 ` compile 'de.greenrobot:greendao-generator:2.1.0'`
 3. 编写创建表语句
	```
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
	        Property fatherId = son.addLongProperty("fatherId").getProperty();
	        son.addToOne(father,fatherId);
	
	        try {
	            //输出到App目录
	            new DaoGenerator().generateAll(schema,"app/src/main/java");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	```
 
 4. 	经过上面几步后，app目录将是这样， ![这里写图片描述](http://img.blog.csdn.net/20170217163601342?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbG92ZXFSYw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 5. 创建测试代码

```
public class MainActivity extends AppCompatActivity {
    private DaoMaster mMaster;
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
    private SonDao mSonDao;
    private FatherDao mFatherDao;


    private void openDb(){
        db=new DaoMaster.DevOpenHelper(this,"person.db",null).getWritableDatabase();
        mMaster=new DaoMaster(db);
        mDaoSession=mMaster.newSession();
        mSonDao=mDaoSession.getSonDao();
        mFatherDao=mDaoSession.getFatherDao();
    }

    private void addPerson(){
        Son son=new Son();
        son.setName("liao");
        son.setAge(18);
        Father father=new Father();
        father.setName("Rc");
        father.setAge(20);
        long fatherId = mFatherDao.insert(father);
        son.setFatherId(fatherId);
        mSonDao.insert(son);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openDb();
        addPerson();
    }
}

```
使用Adb shell参看结果
![这里写图片描述](http://img.blog.csdn.net/20170217164606190?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbG92ZXFSYw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
两张表顺利创建成功，接下来查询数据
![这里写图片描述](http://img.blog.csdn.net/20170217164634565?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbG92ZXFSYw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

##GreenDao查询(懒加载)
```
 private void queryAll(){
//        //方法一：
        List<Son> sons = mSonDao.queryBuilder().list();
//
//
//        //方法二：懒加载方式，只有用到Son对象的方法的时候才加载。
        LazyList<Son> lazyList = mSonDao.queryBuilder().listLazy();
//        //必须释放资源
        lazyList.close();

        //方法三:
        CloseableListIterator<Son> iterator = mSonDao.queryBuilder().listIterator();
        while (iterator.hasNext()){
            Son son = (Son) iterator.next();
            Log.d("liao", "queryAll: "+son);
        }
    }
```
> 可以通过`QueryBuilder.LOG_SQL=true;` 和`QueryBuilder.LOG_VALUES=true;`开启log查看`Sql` 语句

###GreenDao条件查询

```
   private  void queryEq(){
        //eq 等于
        Son son = mSonDao.queryBuilder().where(SonDao.Properties.Name.eq("liao")).unique();
        Log.d("liao", "queryEq: "+son);
    }
    private void queryLike(){
        //Like通配符
        List<Son> sons = mSonDao.queryBuilder().where(SonDao.Properties.Name.like("liao%")).list();
        for (Son son : sons) {
            Log.d("liao", "queryLike: "+son);
        }
    }
    private void queryBetween(){
//        两者之间
        List<Son> list = mSonDao.queryBuilder().where(SonDao.Properties.Age.between(19, 20)).list();
        for (Son son : list) {
            Log.d("liao", "queryBetween: "+son);
        }
    }

    private void queryGt(){
//        大于
        List<Son> list = mSonDao.queryBuilder().where(SonDao.Properties.Age.gt(18)).list();
        for (Son son : list) {
            Log.d("liao", "queryGt: "+son);
        }
    }
    private void queryLt(){
//        小于
        List<Son> list = mSonDao.queryBuilder().where(SonDao.Properties.Age.lt(19)).list();
        for (Son son : list) {
            Log.d("liao", "queryLt: "+son);
        }
    }
    private void queryGe(){
//        大于等于
        List<Son> list = mSonDao.queryBuilder().where(SonDao.Properties.Age.ge(19)).list();
        for (Son son : list) {
            Log.d("liao", "queryGe: "+son);
        }
    }

    private void queryNoteq(){
//        不等于
        List<Son> list = mSonDao.queryBuilder().where(SonDao.Properties.Age.notEq(18)).list();
        for (Son son : list) {
            Log.d("liao", "queryNoteq: "+son);
        }
    }

    private void queryLikeAsc(){
//        升序
        List<Son> list = mSonDao.queryBuilder().orderAsc(SonDao.Properties.Age).list();
        for (Son son : list) {
            Log.d("liao", "queryLikeAsc: "+son);
        }
    }
```
###GreenDao之原生Sql查询

```
  private void querySql(){
        List<Son> list = mSonDao.queryBuilder().where(new WhereCondition.StringCondition("FATHER_ID IN" + "(SELECT _ID FROM FATHER WHERE AGE<21)")).list();
        for (Son son : list) {
            Log.d("liao", "querySql: "+son);
        }
    }
```
### GreenDao之多线程
错误用法
```
 private void queryThread(){
        final Query<Son> build = mSonDao.queryBuilder().build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Son> list = build.list();
                for (Son son : list) {
                    Log.d("liao", "queryThread: "+son);
                }
            }
        }).start();
    }
```
正确用法是
```
private void queryThread(){
        final Query<Son> build = mSonDao.queryBuilder().build();
        new Thread(new Runnable() {
            @Override
            public void run() {
            //使用build创建的线程查询
                List<Son> list = build.forCurrentThread().list();
                for (Son son : list) {
                    Log.d("liao", "queryThread: "+son);
                }
            }
        }).start();
    }
```
###GreenDao之1v1查询

```
    private  void queryOneToOne(){
        List<Son> list = mSonDao.queryBuilder().list();
        for (Son son : list) {
            Log.d(TAG, "queryOneToOne: "+son.getFather().getName());
        }
    }
```

 
###GreenDao之1v多数据结构模型构建
修改创建数据库代码
```
public class MyClass {
    public static void main(String args[]){
        //第一个参数版本号，第二参数包名
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
```
创建数据，一个儿子对应多个父亲。
```
private void addPerson(){
        Son son=new Son();
        son.setName("liao");
        son.setAge(18);
        mSonDao.insert(son);

        Father Kevin=new Father();
        Kevin.setName("kevin");
        Kevin.setAge(20);
        Kevin.setSon(son);
        mFatherDao.insert(Kevin);

        Father tom=new Father();
        tom.setName("tom");
        tom.setAge(40);
        tom.setSon(son);
        mFatherDao.insert(tom);
        }
```
通过adb查看，现在的数据库是这样的：
![这里写图片描述](http://img.blog.csdn.net/20170218091056465?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbG92ZXFSYw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
现在我们用代码查询

```
private void queryOneToMany(){
        List<Son> sonList = mSonDao.queryBuilder().list();
        for (Son son : sonList) {
            List<Father> fathers = son.getFathers();
            for (Father father : fathers) {
                Log.d(TAG, "queryOneToMany: "+"son name:"+son.getName()+"   father name"+father.getName());
            }
        }
    }
```


 

 

	
