package ru.tayupov.kamil.joystick;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SCALE = 10;

    private View thumb;
    private View joystick;
    private TextView centerText;
    private TextView touchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thumb = findViewById(R.id.thumb);
        joystick = findViewById(R.id.joystick);
        centerText = (TextView) findViewById(R.id.center_text);
        touchText = (TextView) findViewById(R.id.touch_text);

        joystick.setOnTouchListener(new View.OnTouchListener() {
            float jRadius = getResources().getDimension(R.dimen.joystick_radius) / 2;
            float tRadius = getResources().getDimension(R.dimen.thumb_radius) / 2;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        moveThumb(event.getX() - jRadius, event.getY() - jRadius);
                        break;
                    case MotionEvent.ACTION_UP:
                        moveThumb(0, 0);
                        break;
                    default:
                }
                return true;
            }

            // Переместить стик
            private void moveThumb(float x, float y) {
                double angle = getAngle(x, -y);
                double radius = getRadius(x, -y);
                double power = getPower(x, -y);
                final double RADIAN = 180 / Math.PI;
                centerText.setText(String.valueOf((int) (angle * RADIAN) + " : " + (int) power));
                touchText.setText(String.valueOf((int) getPower(x) + " : " + (int) getPower(-y)));

                x = (float) (radius * Math.cos(angle)) * jRadius + joystick.getX() + jRadius - tRadius;
                y = -(float) (radius * Math.sin(angle)) * jRadius + joystick.getY() + jRadius - tRadius;

                thumb.setX(x);
                thumb.setY(y);
            }

            // Угол расположения стика
            private double getAngle(float x, float y) {
                double theta;
                x /= jRadius;
                y /= jRadius;
                if (x > 0) {
                    theta = Math.atan(y / x);
                    if (y < 0) {
                        theta += 2 * Math.PI;
                    }
                } else if (x < 0) {
                    theta = Math.atan(y / x) + Math.PI;
                } else {
                    if (y > 0) {
                        theta = Math.PI / 2;
                    } else if (y < 0) {
                        theta = -Math.PI / 2;
                    } else {
                        theta = 0;
                    }
                }
                return theta;
            }

            // Радиус расположения стика
            private double getRadius(float x, float y) {
                x /= jRadius;
                y /= jRadius;
                return Math.min(Math.sqrt(x * x + y * y), (jRadius - tRadius) / jRadius);
            }

            // Скорость перемещения (абсолютная)
            private double getPower(float x, float y) {
                x /= jRadius;
                y /= jRadius;
                double radius = (jRadius - tRadius) / jRadius;
                return Math.min(Math.sqrt(x * x + y * y), radius) / radius * SCALE;
            }

            // Скорость перемещения (по оси X или Y)
            private double getPower(float x) {
                x /= jRadius - tRadius;
                return Math.min(Math.rint(Math.abs(x) * SCALE), SCALE) * Math.signum(x);
            }
        });
    }
}
