package org.mth.jfxemptyfolderscleaner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

/**
 *
 * @author Mattia Marelli
 * @since 21-ott-2014
 */
public class TextLogger {

    public static final String FINISH_MESSAGE = "§";
    public static final String BLANK_MESSAGE = "#";
    private static final int QUEUE_CAPACITY = 1;
    private static boolean timerRunning = false;
    private static final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static TextArea console;
    private static AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long now) {
            try {
                String message = messageQueue.take();

                if (!message.equals(FINISH_MESSAGE) && !message.equals(BLANK_MESSAGE)) {
                    console.appendText(message + "\n");
                } else {
                    timer.stop();
                    timerRunning = false;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    };

    public static void setConsole(TextArea console) {
        TextLogger.console = console;
    }

    public static void publishMessage(String message) {
        if (!timerRunning) {
            timer.start();
            timerRunning = true;
        }

        try {
            messageQueue.put(message);
        } catch (InterruptedException ex) {
            Logger.getLogger(TextLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BlockingQueue<String> getMessageQueue() {
        return messageQueue;
    }

}
