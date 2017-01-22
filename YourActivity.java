public class YourActivity extends DrawerBaseActivity {
FrameLayout fl_content;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_about_app);

        getSupportActionBar().setTitle("Call Vet");

        fl_content = (FrameLayout) findViewById(R.id.fl_content_frame);

        getLayoutInflater().inflate(R.layout.activity_your,fl_content);

}

}
