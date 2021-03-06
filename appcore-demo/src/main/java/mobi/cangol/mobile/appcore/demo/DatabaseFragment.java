package mobi.cangol.mobile.appcore.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import mobi.cangol.mobile.db.CoreSQLiteOpenHelper;
import mobi.cangol.mobile.db.Dao;
import mobi.cangol.mobile.db.DatabaseField;
import mobi.cangol.mobile.db.DatabaseTable;
import mobi.cangol.mobile.db.DatabaseUtils;
import mobi.cangol.mobile.db.QueryBuilder;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 16/4/30.
 */
public class DatabaseFragment extends Fragment {
    private static final String TAG = "DatabaseFragment";
    private ListView listView;
    private Button button0,button1, button2;
    private DataService dataService;
    private AtomicInteger atomicInteger;
    private ArrayAdapter simpleAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.createDataBaseHelper(getActivity());
        dataService = new DataService(getActivity());
        atomicInteger = new AtomicInteger();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        listView = (ListView) this.getView().findViewById(R.id.listview);
        button0 = (Button) this.getView().findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
        button2 = (Button) this.getView().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delData();
            }
        });
        simpleAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,new ArrayList<Data>());
        listView.setAdapter(simpleAdapter);

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadData() {
        simpleAdapter.clear();
        List<Data> list=dataService.findListByName("1");
        simpleAdapter.addAll(list);
    }
    private void addData() {
        Data data=new Data("name_" + atomicInteger.addAndGet(1));
        data.setNickname("nickname_"+new Random().nextInt(100));
        dataService.save(data);
        dataService.refresh(data);
        simpleAdapter.add(data);
    }

    private void delData() {
        if(simpleAdapter.getCount()>0){
            Data data= (Data) simpleAdapter.getItem(simpleAdapter.getCount()-1);
            dataService.delete(data.getId());
            simpleAdapter.remove(data);
        }
    }
}

class DataService implements BaseService<Data> {
    private static final String TAG = "DataService";
    private Dao<Data, Integer> dao;

    public DataService(Context context) {
        try {
            DatabaseHelper dbHelper = DatabaseHelper
                    .createDataBaseHelper(context);
            dao = dbHelper.getDao(Data.class);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService init fail!");
        }
    }

    /**
     * @return the row ID of the newly inserted row or updated row, or -1 if an error occurred
     */
    @Override
    public int save(Data obj) {
        int result = -1;
        try {
            if (obj.getId() > 0 && obj.getId() != -1) {
                result = dao.update(obj);
                if (result > 0) {
                    result = obj.getId();
                }
            } else {
                result = dao.create(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService save fail!");
        }
        return result;
    }

    @Override
    public void refresh(Data obj) {
        try {
            dao.refresh(obj);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService refresh fail!");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService delete fail!");
        }

    }

    @Override
    public Data find(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService find fail!");
        }
        return null;
    }

    @Override
    public int getCount() {
        try {
            return dao.queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService getCount fail!");
        }
        return -1;
    }

    public List<Data> getAllList() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService getAllList fail!");
        }
        return null;
    }

    @Override
    public List<Data> findList(QueryBuilder queryBuilder) {
        return dao.query(queryBuilder);
    }

    public List<Data> findListByName(String name) {
        QueryBuilder queryBuilder = new QueryBuilder(Data.class);
        queryBuilder.addQuery("name", name, "like",true);
        queryBuilder.addQuery("nickname", name, "like",true);
        return dao.query(queryBuilder);
    }

}

class DatabaseHelper extends CoreSQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";
    public static final String DATABASE_NAME = "demo";
    public static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dataBaseHelper;

    public static DatabaseHelper createDataBaseHelper(Context context) {
        if (null == dataBaseHelper) {
            dataBaseHelper = new DatabaseHelper();
            dataBaseHelper.open(context);
        }
        return dataBaseHelper;
    }

    @Override
    public String getDataBaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDataBaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        DatabaseUtils.createTable(db, Data.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade " + oldVersion + "->" + newVersion);
    }


}

@DatabaseTable("TEST_DATA")
class Data {
    @DatabaseField(primaryKey = true, notNull = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String nickname;
    public Data() {
    }

    public Data(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Data{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

interface BaseService<T> {
    /**
     * @param obj
     * @return
     */
    void refresh(T obj) throws SQLException;

    /**
     * @param id
     */
    void delete(Integer id) throws SQLException;

    /**
     * @param id
     * @return
     */
    T find(Integer id) throws SQLException;

    /**
     * @return
     */
    int getCount() throws SQLException;

    /**
     * @return
     */
    List<T> getAllList() throws SQLException;

    /**
     * @param obj
     */
    int save(T obj);

    /**
     * @param queryBuilder
     * @return
     */
    List<T> findList(QueryBuilder queryBuilder);
}