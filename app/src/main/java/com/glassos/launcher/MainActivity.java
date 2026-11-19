package com.glassos.launcher;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.graphics.drawable.Drawable;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    FrameLayout root;
    final String[] defaults={"com.netflix.ninja","com.google.android.youtube.tv","com.android.vending","@files","@gallery","@browser","@hdmi","@settings","@all"};
    String[] slots;
    android.content.SharedPreferences prefs;
    LinearLayout dock, mainTiles;
    TextView clock, date, status;
    final int white = Color.WHITE;
    final int ink = Color.rgb(228,240,255);
    final ArrayList<View> focusables = new ArrayList<>();
    Handler handler = new Handler();
    final Runnable ticker = new Runnable(){ public void run(){ if(clock!=null){clock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));date.setText(new SimpleDateFormat("EEEE, d MMMM", new Locale("ru")).format(new Date()));} handler.postDelayed(this, 30000); }};
    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    GradientDrawable glass(int color, int stroke, int radius){GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{color,Color.argb(105,13,35,53)});g.setCornerRadius(dp(radius));g.setStroke(dp(1),stroke);return g;}
    TextView text(String value,int sp,boolean bold){TextView t=new TextView(this);t.setText(value);t.setTextColor(white);t.setTextSize(sp);t.setGravity(Gravity.CENTER); if(bold)t.setTypeface(null,1);return t;}
    LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setGravity(Gravity.CENTER);return l;}
    LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void focusStyle(View v, GradientDrawable normal, GradientDrawable selected){v.setBackground(normal);v.setFocusable(true);v.setOnFocusChangeListener((view,focused)->{view.setBackground(focused?selected:normal);view.animate().scaleX(focused?1.065f:1).scaleY(focused?1.065f:1).setDuration(145).start();});focusables.add(v);}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);prefs=getSharedPreferences("glassos",MODE_PRIVATE);slots=new String[9];for(int i=0;i<9;i++)slots[i]=prefs.getString("slot"+i,defaults[i]);render();handler.post(ticker);}
    @Override protected void onDestroy(){handler.removeCallbacks(ticker);super.onDestroy();}
    void render(){
        focusables.clear();root=new FrameLayout(this);root.setBackgroundResource(com.glassos.launcher.R.drawable.forest);setContentView(root);
        LinearLayout all=vertical();all.setGravity(Gravity.TOP);all.setPadding(dp(40),dp(20),dp(40),dp(22));root.addView(all,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout top=horizontal();all.addView(top,new LinearLayout.LayoutParams(-1,dp(42)));
        View leftSpacer=new View(this);top.addView(leftSpacer,new LinearLayout.LayoutParams(0,-1,1));
        TextView network=text("⚙",24,false);network.setFocusable(true);network.setOnClickListener(v->openFactorySettings());network.setPadding(dp(15),0,dp(15),0);network.setBackground(glass(0x44608096,0x88ffffff,26));top.addView(network,new LinearLayout.LayoutParams(-2,dp(40)));
        LinearLayout timeArea=vertical();timeArea.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);all.addView(timeArea,new LinearLayout.LayoutParams(-1,0,1));
        clock=text("10:52",100,false);clock.setGravity(Gravity.LEFT|Gravity.BOTTOM);clock.setTextColor(0xe5ffffff);timeArea.addView(clock,new LinearLayout.LayoutParams(-1,dp(112)));
        date=text("",16,false);date.setGravity(Gravity.LEFT);timeArea.addView(date);
        status=text("",12,false);
        mainTiles=horizontal();mainTiles.setGravity(Gravity.CENTER);all.addView(mainTiles,new LinearLayout.LayoutParams(-1,dp(125)));
        addTile("","HDMI",()->openSystem("HDMI"));addTile("","USB",()->openSystem("USB"));addTile("","Приложения",this::showApps);addTile("","Медиа",()->launchKnown("com.android.gallery3d","com.android.music","com.google.android.youtube.tv"));addTile("","Настройки",this::openFactorySettings);
        View gap=new View(this);all.addView(gap,new LinearLayout.LayoutParams(1,dp(24)));
        HorizontalScrollView scroll=new HorizontalScrollView(this);scroll.setHorizontalScrollBarEnabled(false);scroll.setFillViewport(true);all.addView(scroll,new LinearLayout.LayoutParams(-1,dp(96)));
        dock=horizontal();dock.setPadding(dp(12),dp(8),dp(12),dp(8));dock.setBackground(glass(0x9a243b4c,0x99ffffff,27));scroll.addView(dock,new android.widget.FrameLayout.LayoutParams(-1,-1));
        for(int i=0;i<slots.length;i++)addSlot(i);
        if(!focusables.isEmpty())focusables.get(0).requestFocus();
    }
    int[] tileImages={R.drawable.tile0,R.drawable.tile1,R.drawable.tile2,R.drawable.tile3,R.drawable.tile4};
    void addTile(String symbol,String label,Runnable action){
        final int index=mainTiles.getChildCount();
        FrameLayout item=new FrameLayout(this);
        ImageView art=new ImageView(this);art.setImageResource(tileImages[index]);art.setScaleType(ImageView.ScaleType.FIT_CENTER);
        item.addView(art,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(dp(3),0,dp(3),0);mainTiles.addView(item,p);
        item.setFocusable(true);focusables.add(item);
        item.setOnFocusChangeListener((v,focused)->{v.animate().scaleX(focused?1.09f:1).scaleY(focused?1.09f:1).setDuration(140).start();v.setAlpha(focused?1f:.86f);});
        item.setOnClickListener(v->action.run());
    }
    void openFactorySettings(){
        try{startActivity(new Intent(Settings.ACTION_SETTINGS));}
        catch(Exception e){Toast.makeText(this,"Штатные настройки недоступны через Android Intent",Toast.LENGTH_LONG).show();}
    }
    int[] dockImages={R.drawable.dock0,R.drawable.dock1,R.drawable.dock2,R.drawable.dock3,R.drawable.dock4,R.drawable.dock5,R.drawable.dock6,R.drawable.dock7,R.drawable.dock8};
    void addSlot(int index){
        String pkg=slots[index];String label=slotLabel(pkg);int color=slotColor(pkg);
        LinearLayout item=vertical();LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);lp.setMargins(dp(3),0,dp(3),0);dock.addView(item,lp);
        FrameLayout iconBox=new FrameLayout(this);GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{color,0xff263b53});bg.setCornerRadius(dp(15));iconBox.setBackground(glass(0x77608095,0x99ffffff,17));
        ImageView image=new ImageView(this);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setPadding(dp(8),dp(8),dp(8),dp(8));
        Drawable appIcon=null;try{if(!pkg.startsWith("@"))appIcon=getPackageManager().getApplicationIcon(pkg);}catch(Exception ignored){}
        if(appIcon!=null){image.setImageDrawable(appIcon);iconBox.addView(image,new FrameLayout.LayoutParams(-1,-1));}
        else {image.setImageResource(dockImages[index]);iconBox.addView(image,new FrameLayout.LayoutParams(-1,-1));}
        item.addView(iconBox,new LinearLayout.LayoutParams(dp(57),dp(57)));
        TextView caption=text(label,10,false);caption.setSingleLine(true);caption.setEllipsize(android.text.TextUtils.TruncateAt.END);item.addView(caption,new LinearLayout.LayoutParams(-1,dp(19)));
        focusStyle(item,glass(0x00334e63,0x00ffffff,16),glass(0x9977b9e9,0xffd6f8ff,16));
        item.setOnClickListener(v->openSlot(pkg));item.setOnLongClickListener(v->{slotMenu(index);return true;});
    }
    String slotLabel(String p){switch(p){case "@files":return "Файлы";case "@gallery":return "Галерея";case "@browser":return "Браузер";case "@hdmi":return "HDMI";case "@settings":return "Настройки";case "@all":return "Все";default:try{return String.valueOf(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(p,0)));}catch(Exception e){if(p.contains("netflix"))return "Netflix";if(p.contains("youtube"))return "YouTube";if(p.contains("vending"))return "Google Play";return "Приложение";}}}
    String slotSymbol(String p){if(p.contains("netflix"))return "N";if(p.contains("youtube"))return "▶";if(p.equals("@hdmi"))return "▣";if(p.equals("@settings"))return "⚙";if(p.equals("@files"))return "▤";if(p.equals("@browser"))return "◎";if(p.equals("@all"))return "•••";return "✦";}
    int slotColor(String p){if(p.contains("netflix"))return 0xffc41b2a;if(p.contains("youtube"))return 0xffed3131;if(p.contains("vending"))return 0xff27a88c;if(p.equals("@hdmi"))return 0xff239b79;if(p.equals("@settings"))return 0xff607a92;return 0xff3779b8;}
    void openSlot(String pkg){switch(pkg){case "@all":showApps();return;case "@settings":openFactorySettings();return;case "@hdmi":openSystem("HDMI");return;case "@files":try{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivity(i);}catch(Exception e){showApps();}return;case "@browser":launchKnown("com.android.chrome","com.android.browser");return;case "@gallery":launchKnown("com.android.gallery3d","com.google.android.apps.photos");return;default:launchKnown(pkg);}}
    void slotMenu(int index){String[] options={"Открыть","Заменить приложение","Вернуть стандартную иконку","Поменять местами с другой ячейкой"};new AlertDialog.Builder(this).setTitle(slotLabel(slots[index])).setItems(options,(d,n)->{if(n==0)openSlot(slots[index]);else if(n==1)chooseSlot(index);else if(n==2){slots[index]=defaults[index];saveSlots();render();}else swapSlot(index);}).show();}
    void chooseSlot(int index){PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> list=pm.queryIntentActivities(q,0);Collections.sort(list,new ResolveInfo.DisplayNameComparator(pm));String[] names=new String[list.size()];for(int j=0;j<names.length;j++)names[j]=String.valueOf(list.get(j).loadLabel(pm));new AlertDialog.Builder(this).setTitle("Закрепить приложение").setItems(names,(d,n)->{slots[index]=list.get(n).activityInfo.packageName;saveSlots();render();}).setNegativeButton("Отмена",null).show();}
    void swapSlot(int index){String[] names=new String[slots.length];for(int i=0;i<names.length;i++)names[i]=(i+1)+". "+slotLabel(slots[i]);new AlertDialog.Builder(this).setTitle("Переместить в ячейку").setItems(names,(d,n)->{String tmp=slots[n];slots[n]=slots[index];slots[index]=tmp;saveSlots();render();}).show();}
    void saveSlots(){android.content.SharedPreferences.Editor ed=prefs.edit();for(int i=0;i<slots.length;i++)ed.putString("slot"+i,slots[i]);ed.apply();}
    void showQuickPanel(){String[] options={"Wi-Fi","Bluetooth","Звук","Экран и яркость","Все настройки","Выход в заводскую оболочку"};String[] actions={Settings.ACTION_WIFI_SETTINGS,Settings.ACTION_BLUETOOTH_SETTINGS,Settings.ACTION_SOUND_SETTINGS,Settings.ACTION_DISPLAY_SETTINGS,Settings.ACTION_SETTINGS};new AlertDialog.Builder(this).setTitle("GlassOS • Центр управления").setItems(options,(d,n)->{if(n==5){finish();return;}try{startActivity(new Intent(actions[n]));}catch(Exception e){Toast.makeText(this,"Раздел недоступен в этой прошивке",Toast.LENGTH_LONG).show();}}).show();}
    void launchKnown(String... packages){for(String pkg:packages){try{Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i!=null){startActivity(i);return;}}catch(Exception ignored){}}new AlertDialog.Builder(this).setTitle("Приложение не найдено").setMessage("Нужное приложение не установлено или недоступно на этом проекторе.").setPositiveButton("OK",null).show();}
    void openSystem(String type){new AlertDialog.Builder(this).setTitle(type).setMessage("HDMI — отдельное заводское приложение. Выбери его через список приложений, если оно доступно, и закрепи в Dock. Прямой запуск зависит от прошивки.").setPositiveButton("Найти приложение",(d,w)->showApps()).setNegativeButton("Закрыть",null).show();}
    void showSettings(){String[] items={"Настройки Android","Центр управления","Сбросить Dock","О GlassOS","Вернуться на заводской экран"};new AlertDialog.Builder(this).setTitle("GlassOS • Настройки").setItems(items,(d,n)->{if(n==0){try{startActivity(new Intent(Settings.ACTION_SETTINGS));}catch(Exception e){Toast.makeText(this,"Настройки недоступны",Toast.LENGTH_SHORT).show();}}else if(n==1)showQuickPanel();else if(n==2)new AlertDialog.Builder(this).setMessage("Вернуть стандартное расположение иконок?").setPositiveButton("Вернуть",(dd,w)->{slots=defaults.clone();saveSlots();render();}).setNegativeButton("Отмена",null).show();else if(n==3)new AlertDialog.Builder(this).setMessage("GlassOS Liquid Glass v2\nAndroid 11 • HY260 Pro\nСторонний лаунчер. Заводская прошивка не изменена.").setPositiveButton("OK",null).show();else finish();}).show();}
    void showApps(){PackageManager pm=getPackageManager();Intent query=new Intent(Intent.ACTION_MAIN);query.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> found=pm.queryIntentActivities(query,0);Collections.sort(found,new ResolveInfo.DisplayNameComparator(pm));ArrayList<ResolveInfo> apps=new ArrayList<>(found);String[] names=new String[apps.size()];for(int i=0;i<apps.size();i++)names[i]=String.valueOf(apps.get(i).loadLabel(pm));new AlertDialog.Builder(this).setTitle("Все приложения").setItems(names,(d,which)->{try{ResolveInfo r=apps.get(which);Intent intent=new Intent(Intent.ACTION_MAIN);intent.addCategory(Intent.CATEGORY_LAUNCHER);intent.setClassName(r.activityInfo.packageName,r.activityInfo.name);startActivity(intent);}catch(Exception e){Toast.makeText(this,"Не удалось открыть приложение",Toast.LENGTH_SHORT).show();}}).setNegativeButton("Закрыть",null).show();}
    void showContext(String label){new AlertDialog.Builder(this).setTitle(label).setItems(new String[]{"Открыть", "Список приложений", "О GlassOS"},(d,which)->{if(which==0){if(label.equals("Приложения")||label.equals("Все"))showApps();else Toast.makeText(this,"Выбери приложение в Dock",Toast.LENGTH_SHORT).show();}else if(which==1)showApps();else showSettings();}).show();}
    @Override public boolean onKeyDown(int key,KeyEvent e){if(key==KeyEvent.KEYCODE_MENU){View f=getCurrentFocus();if(f!=null){showContext("Выбранная плитка");return true;}}return super.onKeyDown(key,e);}
    class MountainWallpaper extends Drawable {
        Paint p=new Paint(3);Random r=new Random(32);
        @Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height();p.setShader(new LinearGradient(0,0,0,h,new int[]{0xff183e5c,0xff7895a7,0xffe3bcb3,0xff234a55},null,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);
            mountain(c,w,h,.43f,0xff486c80,27);mountain(c,w,h,.54f,0xff325f73,19);mountain(c,w,h,.66f,0xff1b4551,24);
            p.setShader(new LinearGradient(0,h*.47f,0,h*.78f,0x99e4f3f2,0x00ffffff,Shader.TileMode.CLAMP));c.drawRect(0,h*.43f,w,h*.78f,p);p.setShader(null);
            mountain(c,w,h,.75f,0xff173c40,35);
            p.setShader(new LinearGradient(0,0,0,h,0x00000000,0xa9001729,Shader.TileMode.CLAMP));c.drawRect(b,p);p.setShader(null);
        }
        void mountain(Canvas c,float w,float h,float y,int color,int bumps){Path path=new Path();path.moveTo(0,h);path.lineTo(0,h*y);Random random=new Random(color);for(int i=1;i<=bumps;i++){float xx=w*i/bumps;float yy=h*(y+random.nextFloat()*.12f-.055f);path.lineTo(xx,yy);}path.lineTo(w,h);path.close();p.setColor(color);c.drawPath(path,p);}
        @Override public void setAlpha(int alpha){} @Override public void setColorFilter(android.graphics.ColorFilter f){} @Override public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}
    }
}
