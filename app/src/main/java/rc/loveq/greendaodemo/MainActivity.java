package rc.loveq.greendaodemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import cn.loveqRc.DaoMaster;
import cn.loveqRc.DaoSession;
import cn.loveqRc.Father;
import cn.loveqRc.FatherDao;
import cn.loveqRc.Son;
import cn.loveqRc.SonDao;
import de.greenrobot.dao.query.CloseableListIterator;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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

//        Son son=new Son();
//        son.setName("liao-tan");
//        son.setAge(18);
//        Father father=new Father();
//        father.setName("Kevin");
//        father.setAge(20);
//        long fatherId = mFatherDao.insert(father);
//        son.setFatherId(fatherId);
//        mSonDao.insert(son);


//        Son anotherson=new Son();
//        anotherson.setName("tom");
//        anotherson.setAge(19);
//        Father anotherFather=new Father();
//        anotherFather.setName("tomcat");
//        anotherFather.setAge(21);
//        long anotherId = mFatherDao.insert(anotherFather);
//        anotherson.setFatherId(anotherId);
//        mSonDao.insert(anotherson);
    }
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

    private void queryOneToMany(){
        List<Son> sonList = mSonDao.queryBuilder().list();
        for (Son son : sonList) {
            List<Father> fathers = son.getFathers();
            for (Father father : fathers) {
                Log.d(TAG, "queryOneToMany: "+"son name:"+son.getName()+"   father name"+father.getName());
            }
        }
    }
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

    private void querySql(){
        List<Son> list = mSonDao.queryBuilder().where(new WhereCondition.StringCondition("FATHER_ID IN" + "(SELECT _ID FROM FATHER WHERE AGE<21)")).list();
        for (Son son : list) {
            Log.d("liao", "querySql: "+son);
        }
    }

    private void queryThread(){
        final Query<Son> build = mSonDao.queryBuilder().build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Son> list = build.forCurrentThread().list();
                for (Son son : list) {
                    Log.d("liao", "queryThread: "+son);
                }
            }
        }).start();
    }
//    private  void queryOneToOne(){
//        List<Son> list = mSonDao.queryBuilder().list();
//        for (Son son : list) {
//            Log.d(TAG, "queryOneToOne: "+son.getFather().getName());
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QueryBuilder.LOG_SQL=true;
        QueryBuilder.LOG_VALUES=true;
        openDb();
        queryOneToMany();
//        addPerson();
//        addPerson();
//        queryAll();
//        queryEq();
//        queryLike();
//        queryBetween();
//        queryGt();
//            queryLt();
//        queryNoteq();
//        queryGe();
//        queryLikeAsc();
//        querySql();
//        queryThread();
//        queryOneToOne();
    }
}
