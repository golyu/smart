package golyu.com.smart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private static boolean model;
    private RxPermissions rxPermissions;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private List<CodeBean> mDatas;
    private HomeAdapter mAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.phones);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bind(true);
        RxView.clicks(fab)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        bind(model);
                        if (model) {
                            model = false;
                            showMessage("切换为跳转模式");
                            fab.setImageResource(R.drawable.ic_dial);
                            fab.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.colorAccent)));
                        } else {
                            model = true;
                            showMessage("切换为拨号模式");
                            fab.setImageResource(R.drawable.ic_call);
                            fab.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.colorPrimary)));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void bind(boolean theme) {
        mAdapter = new HomeAdapter(theme);
        mAdapter.setOnItemClickListener((CodeBean codeBean, int positon) -> {
            callPhone(codeBean, positon);
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    @SuppressLint("MissingPermission")
    private void showDialog(@NonNull CodeBean codeBean) {
        new MaterialDialog.Builder(this)
                .title(R.string.title)
                .content(codeBean.getCode() + "   " + codeBean.getDescription() + " 属于开通服务,你确认你是否要开通")
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    Intent intent = null;
                    String phoneOver = codeBean.getCode().replaceAll("#", "%23");
                    if (model) {
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneOver));
                    } else {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneOver));
                    }
                    if (intent != null) {
                        MainActivity.this.startActivity(intent);
                    }
                }).show();
    }

    private void showMessage(String msg) {
        Snackbar.make(fab, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @SuppressLint("MissingPermission")
    private void callPhone(CodeBean codeBean, int positon) {
        rxPermissions
                .request(Manifest.permission.CALL_PHONE)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = null;
                        String phoneOver = codeBean.getCode().replaceAll("#", "%23");
                        if (model) {
                            if (codeBean.isType()) {
                                showDialog(codeBean);
                            } else {
                                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneOver));
                            }
                        } else {
                            if (codeBean.isType()) {
                                showDialog(codeBean);
                            } else {
                                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneOver));
                            }

                        }
                        if (intent != null) {
                            MainActivity.this.startActivity(intent);
                        }

                    } else {
                        showMessage("没有拨打电话的权限");
                    }
                });

    }


    protected void initData() {
        mDatas = new ArrayList<CodeBean>();
        mDatas.add(new CodeBean("*888#", "查询话费", false));
        mDatas.add(new CodeBean("*087*0#", "停用流量", true));
        mDatas.add(new CodeBean("*087*101#", "1$换500M流量 30天", true));
        mDatas.add(new CodeBean("*087*300#", "3$换2G流量 30天", true));
        mDatas.add(new CodeBean("*087*500#", "5$换4G流量 30天", true));
        mDatas.add(new CodeBean("*087*1000#", "10$换8.5G流量 30天", true));
        mDatas.add(new CodeBean("*087*888#", "查询087套餐", false));
        mDatas.add(new CodeBean("*700*100#", "1$换125$(=5GB流量) 7天", true));
        mDatas.add(new CodeBean("*700*200#", "2$换10G流量 7天", true));
        mDatas.add(new CodeBean("*700*800#", "8$换40G流量 30天", true));
        mDatas.add(new CodeBean("*700*888#", "查询换购的余额和终止日期", false));
        mDatas.add(new CodeBean("*656*100#", "1$换30$ 7天,网速快,自动续费", true));
        mDatas.add(new CodeBean("*656*0#", "查询流量656套餐", false));
        mDatas.add(new CodeBean("*1333#", "1$换333$,7天,网速慢,自动续费", true));
        mDatas.add(new CodeBean("*1333*888#", "查询1333套餐", false));
        mDatas.add(new CodeBean("*203#", "开通超长待机", true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnItemClickListener {
        void onItemClick(CodeBean codeBean, int position);
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private OnItemClickListener onItemClickListener;
        private boolean themeColor;

        public HomeAdapter(boolean model) {
            this.themeColor = model;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.item, parent,
                    false), themeColor);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.codeView.setText(mDatas.get(position).getCode());
            holder.descriptionView.setText(mDatas.get(position).getDescription());
            if (onItemClickListener != null) {
                holder.onclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(mDatas.get(position), position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView codeView;
            TextView descriptionView;
            LinearLayout onclick;
            CardView cardView;

            public MyViewHolder(View view, boolean themeColor) {
                super(view);
                codeView = (TextView) view.findViewById(R.id.code_view);
                descriptionView = (TextView) view.findViewById(R.id.description_view);
                onclick = (LinearLayout) view.findViewById(R.id.onclick);
                cardView = (CardView) view.findViewById(R.id.card_view);
                if (themeColor) {
                    cardView.setCardBackgroundColor(MainActivity.this.getResources().getColor(R.color.colorPrimary));
                } else {
                    cardView.setCardBackgroundColor(MainActivity.this.getResources().getColor(R.color.colorAccent));
                }
            }
        }

    }
}
