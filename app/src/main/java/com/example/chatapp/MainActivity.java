package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.lang.Double.NaN;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    static ImageButton button;
    Button button2;
    Button button4;
    static TextView textView;
    static TextView recieverText;
    Button startButton;
    static Connection connection;
    public static Activity staticActivity;

    public void setStaticActivity() {
        staticActivity = this;
    }

    static String roomId = "nodata";
    public static boolean relogin = false;
    private ImageView img;
    private ImageView img2;
    private double ax, ay;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start_screen);
            setStaticActivity();
            img = findViewById(R.id.splatoon);
            img2 = findViewById(R.id.img2);
            setTitle("Добро пожаловать!");
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String serverAddress = "82.151.126.74";          //82.151.126.74
            int serverPort = 2156;
            Socket socket = new Socket(serverAddress, serverPort);
            Connection.createInstance(socket);
            connection = Connection.getInstance();
            connection.receive();
            if (Container.getLogin().equals("") && Container.getRoom_login().equals("")) {
                startButton = findViewById(R.id.next);
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LoginOrCreateUserActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                HardMessage msg;
                HardMessage hmsg = new HardMessage();
                hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_USER);
                hmsg.setStuff(new String[]{Container.getLogin(), Container.getPassword()});
                connection.send(hmsg);
                msg = (HardMessage) connection.receive();
                Container.setNickname(msg.getData());
                hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_ROOM);
                hmsg.setStuff(new String[]{Container.getRoom_login(), Container.getRoom_password()});
                connection.send(hmsg);
                msg = (HardMessage) connection.receive();
                Intent intent = new Intent(this, ChatActivitry.class);
                intent.putExtra("RoomName", msg.getData());
                startActivity(intent);
            }
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ошибка!")
                    .setMessage("Вы отключились!")
                    .setCancelable(false)
                    .setNegativeButton("Перезагрузить приложение",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Container.nullate();
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    HardMessage message1 = new HardMessage();
                                    message1.setType(MessageType.EXIT_PROGRAM);
                                    try {
                                        connection.send(message1);
                                    } catch (Exception e) {

                                    }
                                }
                            }).setNeutralButton("Выйти из приложения", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        ActivityCompat.finishAffinity(MainActivity.this);
                    }
                }
            }).setPositiveButton("Перезайти в комнату", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HardMessage message1 = new HardMessage();
                    message1.setType(MessageType.EXIT_PROGRAM);
                    try {
                        connection.send(message1);
                    } catch (Exception e) {

                    }
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static long back_pressed;


    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Нажмите кнопку 'назад' ещё раз, чтобы выйти из приложения!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    private boolean check(String address, int port) {

        try {
            Socket soc = new Socket();
            soc.setSoTimeout(2000);
            soc.connect(new InetSocketAddress(address, port));
            soc.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            if(img != null && img2 != null) {
                ax = event.values[0];
                ay = event.values[1];
                ax = Math.sqrt(Math.abs(ax)) * (ax / Math.abs(ax)) * 10 * -1;
                ay = Math.sqrt(Math.abs(ay)) * (ay / Math.abs(ay)) * 10;
                if (Double.isNaN(ax) || Double.isInfinite(ax)) {
                    ax = 0;
                }
                if (Double.isNaN(ay) || Double.isInfinite(ay)) {
                    ay = 0;
                }
                if (img == null) {
                    img = findViewById(R.id.splatoon);
                }
                if (img2 == null) {
                    img2 = findViewById(R.id.img2);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            img.setTranslationX((float) ax);
                            img.setTranslationY((float) ay);
                            img2.setTranslationX((float) ax * 2f);
                            img2.setTranslationY((float) ay * 2f);
                        }catch (NullPointerException e){

                        }

                    }
                });
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
        /*try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            super.onCreate(savedInstanceState);
            String serverAddress = "192.168.1.39";          //82.151.126.74
            int serverPort = 2156;
            Socket socket = new Socket(serverAddress, serverPort);
            connection = new Connection(socket);
            connection.receive();
            try {
                startProcess(connection);
            } catch (Exception e) {
                printError(e);
            }
        } catch (UnknownHostException e) {
            printError(e);
        } catch (IOException e) {
            printError(e);
        }

    }



    private void onStartButton(final Connection connection) {
        setContentView(R.layout.login_or_register);
        setTitle("Окно пользователя");
        button2 = (Button) findViewById(R.id.join_room);
        button4 = (Button) findViewById(R.id.login_room);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(connection);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(connection);
            }
        });
    }

    private void registerUser(final Connection connection) {
        setContentView(R.layout.register_user);
        setTitle("Создать пользователя");
        Button button6 = findViewById(R.id.button6);
        final EditText login = findViewById(R.id.register_login);
        final EditText password = findViewById(R.id.register_password);
        final EditText nickname = findViewById(R.id.register_nickname);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HardMessage hmsg = new HardMessage();
                    hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_REGISTER_USER);
                    hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString(), nickname.getText().toString()});
                    connection.send(hmsg);
                    Message msg;
                    while (true) {
                        msg = connection.receive();
                        if (msg.getType() == MessageType.CHECK_USER_REGISTER_YES) {
                            doRoomStuff();
                            break;
                        } else if (msg.getType() == MessageType.CHECK_USER_REGISTER_NO) {
                            TextView error = findViewById(R.id.message2);
                            error.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                } catch (IOException e) {
                    printError(e);
                }
            }
        });
    }

    private void loginUser(final Connection connection) {
        setContentView(R.layout.login_user);
        setTitle("Войти в пользователя");
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    EditText tiet1 = findViewById(R.id.login_login);
                    EditText tiet21 = findViewById(R.id.login_password);
                    //Message tmpMessage = new Message();
                    //tmpMessage.setType(MessageType.CHECK_USER_LOGIN_LOGIN);
                    //tmpMessage.setData(tiet1.getText().toString());
                    //connection.send(tmpMessage);
                    //tmpMessage.setType(MessageType.CHECK_USER_LOGIN_PASSWORD);
                    //tmpMessage.setData(tiet21.getText().toString());
                    //connection.send(tmpMessage);
                    HardMessage hmsg = new HardMessage();
                    hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_USER);
                    hmsg.setStuff(new String[]{tiet1.getText().toString(), tiet21.getText().toString()});
                    connection.send(hmsg);
                    Message msg;
                    while (true) {
                        msg = connection.receive();
                        if (msg.getType() == MessageType.CHECK_USER_LOGIN_NO) {
                            findViewById(R.id.message).setVisibility(View.VISIBLE);
                            break;
                        } else if (msg.getType() == MessageType.CHECK_USER_LOGIN_YES) {

                            doRoomStuff();
                            break;
                        }
                    }
                } catch (IOException e) {
                    printError(e);
                }
            }
        });

    }




    private void doRoomStuff() {
        setContentView(R.layout.join_or_create_room);
        setTitle("Окно комнаты");
        Button join_room = findViewById(R.id.join_room);
        final Button create_room = findViewById(R.id.login_room);
        join_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setContentView(R.layout.join_room);
                    setTitle("Войти в комнату");
                    HardMessage tmpMessage = new HardMessage();
                    tmpMessage.setType(MessageType.LOGIN_ROOM_IN_CHECK);
                    connection.send(tmpMessage);

                    HardMessage message = (HardMessage) connection.receive();
                    TextView textView = findViewById(R.id.not_allowed_rooms);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                    String[] rooms = message.getStuff();
                    String jojo = "\n";
                    for (int i = 0; i < rooms.length; i += 2) {
                        jojo += rooms[i];
                        jojo += "";
                        if (rooms[i + 1].equals("true")) {
                            jojo += "Пароль есть.";
                            jojo += "\n";
                        } else if (rooms[i + 1].equals("false")) {
                            jojo += "Пароля нет.";
                            jojo += "\n";
                        }
                    }
                    textView.setText(textViсw.getText() + jojo);
                    Button starrrt = findViewById(R.id.next);
                    final EditText login = findViewById(R.id.login_room);
                    final EditText password = findViewById(R.id.password_room);
                    starrrt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                HardMessage hmsg = new HardMessage();
                                hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_ROOM);
                                hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString()});
                                connection.send(hmsg);
                                Message msg;
                                while (true) {
                                    msg = connection.receive();
                                    if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_EXISTS) {
                                        Intent intent = new Intent(MainActivity.this, ChatActivitry.class);
                                        intent.putExtra("RoomName", msg.getData());
                                        startActivity(intent);
                                        finish();
                                        break;
                                    } else if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_DOESNT_EXIST) {
                                        TextView textView1 = findViewById(R.id.textView14);
                                        textView1.setVisibility(View.VISIBLE);
                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                printError(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    printError(e);
                }
            }
        });
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.create_room);
                setTitle("Создать комнату");
                try {
                    HardMessage tmpMessage = new HardMessage();
                    tmpMessage.setType(MessageType.CREATE_ROOM_IN_CHECK);
                    connection.send(tmpMessage);
                    HardMessage message = (HardMessage) connection.receive();
                    TextView textView = findViewById(R.id.not_allowed_rooms);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                    String[] rooms = message.getStuff();
                    String jojo = "\n";
                    for (int i = 0; i < rooms.length; i += 2) {
                        jojo += rooms[i];
                        jojo += "";
                        if (rooms[i + 1].equals("true")) {
                            jojo += "Пароль есть.";
                            jojo += "\n";
                        } else if (rooms[i + 1].equals("false")) {
                            jojo += "Пароля нет.";
                            jojo += "\n";
                        }
                    }
                    textView.setText(textView.getText() + jojo);
                    Button starrrt = findViewById(R.id.next);
                    final EditText login = findViewById(R.id.login_room);
                    final EditText password = findViewById(R.id.password_room);
                    starrrt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                HardMessage hmsg = new HardMessage();
                                hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_REGISTER_ROOM);
                                hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString()});
                                connection.send(hmsg);
                                Message msg;
                                while (true) {
                                    msg = connection.receive();
                                    if (msg.getType() == MessageType.ROOM_CREATE_EXISTS) {
                                        TextView textView1 = findViewById(R.id.textView14);
                                        textView1.setVisibility(View.VISIBLE);
                                        break;
                                    } else if (msg.getType() == MessageType.CREATE_ROOM_OKAY) {
                                        Intent intent = new Intent(MainActivity.this, ChatActivitry.class);
                                        intent.putExtra("RoomName", msg.getData());
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                printError(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void launchEverything(String roomName) {
        setContentView(R.layout.activity_main);
        setTitle(roomName);
        button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.ReaderAndWriter.shouldIRead = true;
            }
        });
        Button reloginButton = findViewById(R.id.relogin);
        reloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HardMessage msg = new HardMessage();
                msg.setType(MessageType.I_WANNA_RELOGIN);
                try {
                    connection.send(msg);
                    doRoomStuff();
                } catch (IOException e) {
                    printError(e);
                }
            }
        });
        textView = (TextView) findViewById(R.id.chat);
        textView.setMovementMethod(new ScrollingMovementMethod());
        recieverText = (TextView) findViewById(R.id.TextInputEditTextRoar);
        Client.ReaderAndWriter.button = button;
        Client.ReaderAndWriter.textView = textView;
        Client.ReaderAndWriter.recieverText = recieverText;
        String host = "192.168.1.57";
        int hostint = 2156;
        startService(new Intent(MainActivity.this, SuperDuperService.class));
    }


    public static List<Object> getAllThisStuff() {
        List<Object> stuff = new ArrayList<>();
        stuff.add(button);
        stuff.add(textView);
        stuff.add(recieverText);
        stuff.add(connection);
        return stuff;
    }

    private void printError(Throwable e) {
        setContentView(R.layout.fail);
        TextView text = findViewById(R.id.fail_type);
        text.setText(e.getMessage());
        TextView text2 = findViewById(R.id.fail_reason);
        text2.setText((CharSequence) e.getCause());
        e.printStackTrace();
    }*/
    //Stuff that launches everything
    /*
    if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                button = (Button) findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Client.ReaderAndWriter.shouldIRead = true;
                    }
                });
                textView = (TextView) findViewById(R.id.chat);
                textView.setMovementMethod(new ScrollingMovementMethod());
                recieverText = (TextView) findViewById(R.id.TextInputEditTextRoar);
                Client.ReaderAndWriter.button = button;
                Client.ReaderAndWriter.textView = textView;
                Client.ReaderAndWriter.recieverText = recieverText;
                String host = "192.168.1.57";
                int hostint = 2156;
                startService(new Intent(MainActivity.this, SuperDuperService.class));
    */

}


