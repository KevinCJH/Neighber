package orbital.raspberry.neighber;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.introbtn)
                        .image(R.mipmap.intro1)
                        .title("What is Neighber?")
                        .description("Neighber allows you to borrow or lend items anytime, anywhere!")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.introbtn)
                .image(R.mipmap.intro2)
                .title("Need an item?")
                .description("You can start borrowing for items you need by browsing the main page or posting a request!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.introbtn)
                .image(R.mipmap.intro3)
                .title("Feeling Generous?")
                .description("Add a new post or browse requests to lend your items to other users. Sharing is caring!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.introbtn)
                .image(R.mipmap.intro4)
                .title("Need help?")
                .description("Feel free to contact us via Feedback, it is located on the top right menu!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.introbtn)
                .image(R.mipmap.intro5)
                .title("Are you ready?")
                .description("Let us begin building a wonderful community and foster the Kampung spirit!")
                .build());

    }


    @Override
    public void onFinish() {
        super.onFinish();
       // Toast.makeText(this, "Try this library in your project! :)", Toast.LENGTH_SHORT).show();
    }

}
