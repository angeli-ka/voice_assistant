package com.example.myapplication

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.db.NoteManager
import com.example.myapplication.models.Note
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity()  {
    private val REQuEST_CODE = 21758;
    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    val BROWSER_COMMAND = "найди в интернете";
    val OPEN_APP_COMMAND = "открой";
    val CALLPHONE_COMMAND = "позвони абоненту";
    val CREATE_NOTE_COMMAND:String = "создай заметку с текстом";
    val OPEN_NOTES_COMMAND:String = "список заметок";
    val SET_ALARM_COMMAND:String = "поставь будильник на";
    val HELP_ME_COMMAND: String = "мне плохо";

    private val RECORD_REQUEST_CODE = 21759;
    lateinit var mTTS:TextToSpeech
    var noteManager = NoteManager(this);

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Устанавливаем интерфейс
        setContentView(R.layout.activity_main)

        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                mTTS.language = Locale.getDefault();
            }
        })

        executeButton.setOnClickListener {
            OnVoiceCallback(editInput.text.toString());
        }

        voiceBtn.setOnClickListener {
            // Проверяем разрешения.
            val contactsReadStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            val callPhoneStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            // Проверяем, имеются ли у приложения разрешения к действиям чтения контактов и звонка
            if (contactsReadStatus == PackageManager.PERMISSION_DENIED || callPhoneStatus == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
                    RECORD_REQUEST_CODE)
            }
            // Если разрешения имеются, то
            else
            {

                //speak();
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите что-нибудь")

                //startActivityForResult(intent, REQuEST_CODE);

                try {
                    activityResultLauncher.launch(intent)
                }
                catch (e: Exception)
                {
                    Toast.makeText(this, "Device is not supported", Toast.LENGTH_SHORT).show()
                }
            }
        }


        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result: ActivityResult? ->

            if (result!!.resultCode == RESULT_OK && result!!.data != null)
            {
                var speechText = result!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<Editable>
                textResult.text = speechText[0]
                OnVoiceCallback(speechText[0].toString());
            }
        }

    }

    private fun SearchInBrowserCommand(query: String)
    {
        mTTS.speak("Вот, что мне удалось найти по запросу: " + query, TextToSpeech.QUEUE_FLUSH, null)
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, query)
        startActivity(intent)
    }

    private fun OpenApp(appName: String)
    {
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (currentPackage in packages)
        {

            if (appName.contains(currentPackage.loadLabel(packageManager).toString().lowercase()))
            {
                val launchIntent = packageManager.getLaunchIntentForPackage(currentPackage.packageName);

                if (launchIntent != null)
                {
                    mTTS.speak("Открываю " + appName, TextToSpeech.QUEUE_FLUSH, null)
                    startActivity(launchIntent);

                }

            }
        }
    }

    private fun CallPhone(contactName :String)
    {
        val resolver: ContentResolver = contentResolver;
        val cur = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null) {
            if (cur.count > 0)
            {
                while (cur.moveToNext())
                {
                    val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID).toInt())
                    val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME).toInt());
                    val hasNumber = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER).toInt()).toInt();

                    if (contactName.lowercase() == name.lowercase())
                    {

                        mTTS.speak("Звоню абоненту - " + name, TextToSpeech.QUEUE_FLUSH, null)
                        if (hasNumber > 0)
                        {
                            val cursorPhone = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)
                            if (cursorPhone != null && cursorPhone.count > 0)
                            {
                                while (cursorPhone.moveToNext())
                                {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER).toInt())
                                    textResult.text = phoneNumValue;
                                    val phoneIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumValue));

                                    startActivity(phoneIntent);

                                    /*try {
                                        callActivityLauncher.launch(phoneIntent);
                                    }
                                    catch (e: Exception)
                                    {

                                    }

                                    callActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                                        Toast.makeText(this, result!!.resultCode, Toast.LENGTH_LONG);
                                    }*/
                                }
                            }
                        }
                        else
                        {
                            mTTS.speak("К сожалению, в вашей записной книге нет номера этого контакта", TextToSpeech.QUEUE_FLUSH, null)
                        }
                    }
                }
            }
        }

    }

    private fun HelpMe()
    {
        val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
        //honeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(phoneIntent);
    }

    fun CreateNoteCommand(noteBody: String)
    {
        noteManager?.OpenDatabase();
        noteManager?.AddNote(Note(null, noteBody, null));
        mTTS.speak("Готово. Заметка создана !", TextToSpeech.QUEUE_FLUSH, null)
        textResult.text = "Заметка успешно создана !";
    }

    fun CreateAlarmCommand(fullCommand: String)
    {
        val lstValues: List<String> = fullCommand.split(":").map { it -> it.trim() }
        val hour: Int = lstValues.get(0).toInt();
        val minute: Int = lstValues.get(1).toInt();

        val calendar: Calendar = Calendar.getInstance()
        if (Build.VERSION.SDK_INT >= 23) {
            calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute,
                0
            )
        } else {
            calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute,
                0
            )
        }
        textResult.text = "Будильник установлен !";
        setAlarm(calendar.timeInMillis);
        //Toast.makeText(this, calendar.time.time.seconds.toString(), Toast.LENGTH_LONG).show();
        //textResult.text = hour + ":" + minute;

    }

    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            0,
            pendingIntent
        )

        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
    }

    private fun OnVoiceCallback(result: String)
    {
        val loweredQuery = result.lowercase();
        //Toast.makeText(this, loweredQuery, Toast.LENGTH_LONG).show();
        if (loweredQuery.contains(BROWSER_COMMAND))
        {
            SearchInBrowserCommand(loweredQuery.replace(BROWSER_COMMAND, ""));
            return;
        }
        if (loweredQuery.contains((OPEN_APP_COMMAND)))
        {
            OpenApp(loweredQuery.replace(OPEN_APP_COMMAND, ""));
            return;
        }
        if (loweredQuery.contains(CALLPHONE_COMMAND))
        {
            CallPhone(loweredQuery.replace(CALLPHONE_COMMAND + " ", ""));
            return;
        }

        if (loweredQuery.contains(OPEN_NOTES_COMMAND))
        {
            val intent = Intent(this, NoteActivity::class.java);
            startActivity(intent);
            return;
        }

        if (loweredQuery.contains(CREATE_NOTE_COMMAND))
        {
            CreateNoteCommand(loweredQuery.replace(CREATE_NOTE_COMMAND + " ", ""));
            return;
        }
        if (loweredQuery.contains(SET_ALARM_COMMAND))
        {
            CreateAlarmCommand(loweredQuery.replace(SET_ALARM_COMMAND + " ", ""));
            return;
        }
        if (loweredQuery.contains(HELP_ME_COMMAND))
        {
            Toast.makeText(this, "UUU", Toast.LENGTH_LONG).show();
            HelpMe();
            return;
        }



        mTTS.speak("Я вас не понимаю.", TextToSpeech.QUEUE_FLUSH, null)

    }

    override fun onDestroy() {
        super.onDestroy();
        noteManager.CloseDatabase();
    }

    /*override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        Toast.makeText(this, resultCode, Toast.LENGTH_LONG);
        //super.onActivityResult(requestCode, resultCode, data)
    }*/


}
public class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "!!!!!", Toast.LENGTH_LONG).show();
        createNotificationChannel(context)
        notifyNotification(context)
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Голосовой помощник",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Голосовой помощник")
                .setContentText("Будильник сработал")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID, build.build())

        }

    }

}