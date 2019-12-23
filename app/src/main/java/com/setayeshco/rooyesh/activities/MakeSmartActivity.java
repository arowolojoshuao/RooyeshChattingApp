package com.setayeshco.rooyesh.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rey.material.widget.Slider;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.main.MainActivity;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.bluetooth.ChatController;
import com.setayeshco.rooyesh.helpers.AppHelper;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;

public class MakeSmartActivity extends AppCompatActivity {
	
	
	@BindView(R.id.app_bar)
	android.support.v7.widget.Toolbar toolbar;
	
	
	// int SPLASH_TIME_OUT = 500;
	
	
	//..........
	
	Vibrator v;
	private TextView status;
	private TextView txtBlueToothStatus;
	private Button btnConnect;
	//  private Button btn1;
	//   private Button btn2;
	//   private Button btn3;
	//  private Button btn4;
	//  private Button btn5;
	//   private Button btn6;
	
	private Button btnLampFront;
	private Button btnLampBack;
	private Button btnPardehDown;
	private Button btnPardeh;
	private Button btnProjector;
	
	private ListView listView;
	private Dialog dialog;
	private TextInputLayout inputLayout;
	private ArrayAdapter<String> chatAdapter;
	private ArrayList<String> chatMessages;
	private BluetoothAdapter bluetoothAdapter;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_OBJECT = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String DEVICE_OBJECT = "device_name";
	
	private static final int REQUEST_ENABLE_BLUETOOTH = 1;
	private static ChatController chatController;
	private BluetoothDevice connectingDevice;
	private ArrayAdapter<String> discoveredDevicesAdapter;
	final char CR = (char) 0x0D;
	final char LF = (char) 0x0A;
	
	final String CRLF = "" + CR + LF;
	
	private static String address = "";
	
	//..........
	
	
	// Switch switchlamp;
	//  Switch switchprojector;
	//   Switch switchPardeh;
	//   Switch switchSound;
	
	@SuppressLint("RestrictedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_smart);
		
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		//  switchlamp      = findViewById(R.id.switchlamp);
		//    switchprojector = findViewById(R.id.switchprojector);
		//    switchPardeh    = findViewById(R.id.switchPardeh);
		//    switchSound    = findViewById(R.id.switchSound);
		//    switchlamp.setChecked(false);
		//     switchprojector.setChecked(false);
		//     switchPardeh.setChecked(false);
		//    switchSound.setChecked(false);
      /*  switchlamp.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (switchlamp.isChecked()){
                    switchlamp.setChecked(false);
                }
            }
        });*/
		setTypeFaces();
		
		toolbar = findViewById(R.id.app_bar_make_smart);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("رویش");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			// getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
			
		}
		
		DrawerLayout drawer = findViewById(R.id.drawer_make_smart);
		// EventBus.getDefault().register(this);
		//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

   /*     ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
      toggle.setDrawerIndicatorEnabled(false);
      drawer.addDrawerListener(toggle);
      toggle.setDrawerIndicatorEnabled(true);
      drawer.setDrawerListener(toggle);
      toggle.syncState();*/
		
		
		//lllllllll;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;lllllllllllllllllllllllll
		
		
		Slider sliderVolume = findViewById(R.id.sliderVolume);
		
		//  MaterialSpinner spinner_curtain = (MaterialSpinner) findViewById(R.id.spinner_curtain);
     /*   MaterialSpinner spinner_lamp = (MaterialSpinner) findViewById(R.id.spinner_lamp);
        spinner_lamp.setEnabled(false);
        spinner_lamp.setItems("لامپ های جلو", "لامپ های عقب", "همه لامپ ها");*/
		// spinner_curtain.setItems("بالا", "پایین");

   /*     spinner_lamp.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                if (item.equals("همه لامپ ها")){
                    startActivity(new Intent(MakeSmartActivity.this, BluetoothActivity.class));
                }
            }
        });*/

   /*     spinner_curtain.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });*/
/*

switchlamp.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(Switch view, boolean checked) {

        if (switchlamp.isChecked()) {
            Intent intent = new Intent(MakeSmartActivity.this, BluetoothActivity.class);
            intent.putExtra("device_selected", "lamp");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            MakeSmartActivity.this.finish();
        }

      */
/*  if (switchlamp.isChecked()){
            spinner_lamp.setEnabled(true);
        }else {
            spinner_lamp.setEnabled(false);
        }*//*

    }


});
*/



        /*switchPardeh.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (switchPardeh.isChecked()) {
                    Intent intent = new Intent(MakeSmartActivity.this, BluetoothActivity.class);
                    intent.putExtra("device_selected", "Pardeh");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    MakeSmartActivity.this.finish();
                }

            }

        });*/

    /*    switchSound.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (switchSound.isChecked()) {
                    Intent intent = new Intent(MakeSmartActivity.this, BluetoothActivity.class);
                    intent.putExtra("device_selected", "Sound");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    MakeSmartActivity.this.finish();
                }

            }

        });*/
 /*       switchprojector.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {

                if (switchprojector.isChecked()) {
                    Intent intent = new Intent(MakeSmartActivity.this, BluetoothActivity.class);
                    intent.putExtra("device_selected", "projector");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    MakeSmartActivity.this.finish();
                }

            }

        });*/
		
		
		//jjjjjjjjjj
		
		
		findViewsByIds();

  /*      Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("device_selected")){
            if (bundle.getString("device_selected").equals("lamp")){
                txtBlueToothStatus.setText("تغییر وضعیت لامپ ها");
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.GONE);
            }else if(bundle.getString("device_selected").equals("Pardeh")){
                txtBlueToothStatus.setText("تغییر وضعیت پرده ها");
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.VISIBLE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.GONE);
            }else if(bundle.getString("device_selected").equals("projector")){
                txtBlueToothStatus.setText("تغییر وضعیت پروژکتور");
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.VISIBLE);
            }else if(bundle.getString("device_selected").equals("Sound")){
                txtBlueToothStatus.setText("تغییر وضعیت صدا");
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.VISIBLE);
                btn5.setVisibility(View.GONE);
            }
        }*/
		
		//check device support bluetooth or not
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "بلوتوث در دسترس نیست!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		//show bluetooth devices dialog when click connect button
		btnConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................
				
				showPrinterPickDialog();
			}
		});
		
		//set chat adapter
		chatMessages = new ArrayList<>();
		chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
		listView.setAdapter(chatAdapter);
		
		//jjjjjjjjjj
		
		
	}
	
	
	//......................................
	
	
	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case ChatController.STATE_CONNECTED:
							setStatus("وصل شدن به : " + connectingDevice.getName());
							btnConnect.setEnabled(false);
							break;
						case ChatController.STATE_CONNECTING:
							setStatus("در حال مرتبط سازی...");
							btnConnect.setEnabled(false);
							break;
						case ChatController.STATE_LISTEN:
						case ChatController.STATE_NONE:
							setStatus("ارتباط بر قرار نشد");
							
							//'''''''''''''''''''

                          /*  if (chatController != null)
                                chatController.stop();*/





                        /*    if (chatController != null) {
                                if (chatController.getState() == ChatController.STATE_NONE) {
                                    chatController.start();
                                }
                            }*/


                         /*   if (!bluetoothAdapter.isEnabled()) {
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                            } else {
                                chatController = new ChatController(MakeSmartActivity.this, handler);
                            }*/
							
							btnConnect.setEnabled(true);
							
							
							//'''''''''''''''''''
							
							
							break;
					}
					break;
				case MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					
					String writeMessage = new String(writeBuf);
					chatMessages.add("Me: " + writeMessage);
					chatAdapter.notifyDataSetChanged();
					break;
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					
					String readMessage = new String(readBuf, 0, msg.arg1);
					chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
					chatAdapter.notifyDataSetChanged();
					break;
				case MESSAGE_DEVICE_OBJECT:
					connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
					Toast.makeText(getApplicationContext(), "وصل شدن به " + connectingDevice.getName(),
							Toast.LENGTH_SHORT).show();
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();
					break;
			}
			return false;
		}
	});
	
	private void showPrinterPickDialog() {
		
		try {
			
			
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.layout_bluetooth);
			dialog.setTitle("دستگاه های بلوتوث");
			
			if (bluetoothAdapter.isDiscovering()) {
				bluetoothAdapter.cancelDiscovery();
			}
			bluetoothAdapter.startDiscovery();
			
			//Initializing bluetooth adapters
			ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
			discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
			
			//locate listviews and attatch the adapters
			ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
			ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
			listView.setAdapter(pairedDevicesAdapter);
			listView2.setAdapter(discoveredDevicesAdapter);
			
			// Register for broadcasts when a device is discovered
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(discoveryFinishReceiver, filter);
			
			// Register for broadcasts when discovery has finished
			filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			registerReceiver(discoveryFinishReceiver, filter);
			
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
			
			// If there are paired devices, add each one to the ArrayAdapter
			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			} else {
				pairedDevicesAdapter.add(getString(R.string.none_paired));
			}
			
			
			//Handling listview item click event
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					bluetoothAdapter.cancelDiscovery();
					String info = ((TextView) view).getText().toString();
					address = info.substring(info.length() - 17);
					
					connectToDevice(address);
					dialog.dismiss();
				}
				
			});
			
			listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					bluetoothAdapter.cancelDiscovery();
					String info = ((TextView) view).getText().toString();
					address = info.substring(info.length() - 17);
					
					connectToDevice(address);
					dialog.dismiss();
				}
			});
			
			dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.setCancelable(false);
			dialog.show();
		} catch (Exception ex) {
		}
	}
	
	private void setStatus(String s) {
		status.setText(s);
	}
	
	private void connectToDevice(String deviceAddress) {
		bluetoothAdapter.cancelDiscovery();
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
		chatController.connect(device);
	}
	
	private void findViewsByIds() {
		status = (TextView) findViewById(R.id.status);
		txtBlueToothStatus = (TextView) findViewById(R.id.txtBlueToothStatus);
		btnConnect = (Button) findViewById(R.id.btn_connect);
      /*  btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);*/
		btnLampFront = (Button) findViewById(R.id.btnLampFront);
		btnLampBack = (Button) findViewById(R.id.btnLampBack);
		btnPardeh = (Button) findViewById(R.id.btnPardeh);
		btnPardehDown = (Button) findViewById(R.id.btnPardehDown);
		btnProjector = (Button) findViewById(R.id.btnProjector);
		btnLampFront.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		btnLampBack.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		btnPardeh.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		btnPardehDown.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		btnProjector.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		//    btn6.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		btnConnect.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
		txtBlueToothStatus.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
		status.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
		
		listView = (ListView) findViewById(R.id.list);
		inputLayout = (TextInputLayout) findViewById(R.id.input_layout);
		View btnSend = findViewById(R.id.btn_send);
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputLayout.getEditText().getText().toString().equals("")) {
					Toast.makeText(MakeSmartActivity.this, "Please input some texts", Toast.LENGTH_SHORT).show();
				} else {
					//TODO: here
					final char CR = (char) 0x0D;
					final char LF = (char) 0x0A;
					
					final String CRLF = "" + CR + LF;
					sendMessage(inputLayout.getEditText().getText().toString() + CRLF);
					inputLayout.getEditText().setText("");
				}
			}
		});
		
		btnLampFront.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v1) {
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................


     /*   final  char CR  = (char) 0x0D;
                final  char LF  = (char) 0x0A;

                final  String CRLF  = "" + CR + LF;*/
				txtBlueToothStatus.setText("تغییر وضعیت لامپ ها");
				sendMessage("1" + CRLF);
			}
		});
		btnLampBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v1) {
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................
				
				txtBlueToothStatus.setText("تغییر وضعیت لامپ ها");
				sendMessage("2" + CRLF);
			}
		});
		btnPardeh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v1) {
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................
				
				txtBlueToothStatus.setText("تغییر وضعیت پرده ها");
				sendMessage("3" + CRLF);
			}
		});
		btnPardehDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v1) {
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................
				
				txtBlueToothStatus.setText("تغییر وضعیت پرده ها");
				sendMessage("4" + CRLF);
			}
		});
		btnProjector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v1) {
				
				
				//.....................
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
				} else {
					//deprecated in API 26
					v.vibrate(50);
				}
				
				//.....................
				
				txtBlueToothStatus.setText("تغییر وضعیت پروژکتور");
				//    new Handler().postDelayed(this::launchWelcomeActivity, SPLASH_TIME_OUT);
				
				sendMessage("5" + CRLF);
				
				//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
				
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do something after 5s = 5000ms
						sendMessage("5" + CRLF);
					}
				}, 300);
				
				//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
				
				
			}
		});
    /*    btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("6"+CRLF);
            }
        });*/
		
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == Activity.RESULT_OK) {
					chatController = new ChatController(this, handler);
				} else {
					Toast.makeText(this, "بلوتوث هنوز غیرفعال است، برنامه را خاموش کنید!", Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}
	
	private void sendMessage(String message) {
//        if (chatController.getState() != ChatController.STATE_CONNECTED) {
//            Toast.makeText(this, "اتصال از دست رفت!", Toast.LENGTH_SHORT).show();
//            return;
//        }
		
		if (message.length() > 0) {
			byte[] send = message.getBytes();
			chatController.write(send);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
		} else {
			chatController = new ChatController(this, handler);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (chatController != null) {
			if (chatController.getState() == ChatController.STATE_NONE) {
				chatController.start();
			}
			if (!address.equals("")) {
				bluetoothAdapter.cancelDiscovery();
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
				chatController.connect(device);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//        if (chatController != null)
//            chatController.stop();
		
		if (chatController != null) {
			if (chatController.getState() == ChatController.STATE_NONE) {
				chatController.start();
			}
			if (!address.equals("")) {
				bluetoothAdapter.cancelDiscovery();
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
				chatController.connect(device);
			}
		}
	}
	
	private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				if (discoveredDevicesAdapter.getCount() == 0) {
					discoveredDevicesAdapter.add(getString(R.string.none_found));
				}
			}
		}
	};
	
	
	//......................................
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			startActivity(new Intent(MakeSmartActivity.this, MainActivity.class));
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setTypeFaces() {
		
		TextView textView3 = (TextView) findViewById(R.id.textView3);
		TextView textView7 = (TextView) findViewById(R.id.textView7);
		TextView textView5 = (TextView) findViewById(R.id.textView5);
		TextView textView4 = (TextView) findViewById(R.id.textView4);
		
		TextView txtOn = (TextView) findViewById(R.id.txtOn);
		TextView txtOff = (TextView) findViewById(R.id.txtOff);
		MaterialSpinner spinner_lamp = (MaterialSpinner) findViewById(R.id.spinner_lamp);
		//    MaterialSpinner spinner_curtain =(MaterialSpinner) findViewById(R.id.spinner_curtain);
		if (AppConstants.ENABLE_FONTS_TYPES) {
			textView3.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
			textView7.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
			textView5.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
			textView4.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IRANSans(FaNum)_Bold"));
			txtOn.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
			spinner_lamp.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
			//   spinner_curtain.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
			txtOff.setTypeface(AppHelper.setTypeFace(MakeSmartActivity.this, "IranSans"));
		}
	}
}
