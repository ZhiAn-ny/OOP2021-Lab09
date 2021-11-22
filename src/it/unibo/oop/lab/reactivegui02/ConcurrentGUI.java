package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class ConcurrentGUI extends JFrame{

    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel("0");
    private final JButton stop = new JButton("Stop");
    private final JButton down = new JButton("Down");
    private final JButton up = new JButton("Up");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int)(screenSize.width * WIDTH_PERC), (int)(screenSize.height * HEIGHT_PERC));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel pan = new JPanel();
        pan.add(display);
        pan.add(up);
        pan.add(down);
        pan.add(stop);
        this.setContentPane(pan);
        this.setVisible(true);

        final Agent count = new Agent();
        new Thread(count).start();

        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                count.setDecreasing();
            }
        });
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                count.setIncreasing();
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                count.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });

    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean isIncreasing = true;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter));
                        }
                    });

                    if (this.isIncreasing) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void setIncreasing() {
            this.isIncreasing = true;
        }

        public void setDecreasing() {
            this.isIncreasing = false;
        }
    }
}
