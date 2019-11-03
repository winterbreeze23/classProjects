
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Game implements Runnable {

	public void run() {
        
        final JFrame frame = new JFrame("Mahjong");
        frame.setSize(1040, 1600);
        JLabel status = new JLabel("Match identical characters or word pairs!");
        
        // GameBoard
        final GameBoard board = new GameBoard(status);
        board.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(board, BorderLayout.CENTER);
        board.setLayout(new GridLayout(8, 6));
        
        // Start game
        board.reset();

        // Control panel
        final JPanel control_panel = new JPanel();
        control_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(control_panel, BorderLayout.NORTH);
        
        // Reset button
        final JButton reset = new JButton("New Game");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	board.reset();
            }
        });
        control_panel.add(reset);
        
        // Report window
        
        final JFrame report_frame = new JFrame("Learning Report");
        report_frame.setSize(500, 500);
        report_frame.setLayout(new BorderLayout());
        
        // Print report button
        final JButton reportButton = new JButton("Get Learning Report");
        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	board.writeToReport();
            	BufferedReader br;
            	try {
					br = new BufferedReader(new FileReader("files/report.txt"));
					JTextArea reportText = new JTextArea();
					reportText.setEditable(false);
	            	try {
						reportText.read(br, null);
						
						// create scroll pane
						JScrollPane reportScrollPane = new JScrollPane(reportText);
				        reportScrollPane.setBorder(new EmptyBorder(50, 50, 50, 50));
				        report_frame.add(reportScrollPane, BorderLayout.CENTER);
						
						// Put the frame on the screen
				        report_frame.pack();
				        report_frame.setVisible(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        control_panel.add(reportButton);
        
        // Instructions window
        
        final JFrame instr_frame = new JFrame("Instructions");
        instr_frame.setSize(500, 500);
        instr_frame.setLayout(new BorderLayout());
        
        // Instructions button
        final JButton instrButton = new JButton("Instructions");
        instrButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	BufferedReader br;
            	try {
					br = new BufferedReader(new FileReader("files/instructions.txt"));
					JTextArea instrText = new JTextArea();
					instrText.setEditable(false);
	            	try {
	            		instrText.read(br, null);
						
						// create scroll pane
						JScrollPane instrScrollPane = new JScrollPane(instrText);
						instrScrollPane.setBorder(new EmptyBorder(50, 50, 50, 50));
						instr_frame.add(instrScrollPane, BorderLayout.CENTER);
						
						// Put the frame on the screen
						instr_frame.pack();
						instr_frame.setVisible(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        control_panel.add(instrButton);
        
        // Status panel
        final JPanel status_panel = new JPanel();
        status_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(status_panel, BorderLayout.SOUTH);
        status_panel.add(status);
        
        // Panel for word pairs
        final JPanel words_panel = new JPanel();
        words_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        //words_panel.setBackground(new Color(0, 0, 0, 80));
        //frame.add(words_panel, BorderLayout.EAST);
        words_panel.setLayout(new BoxLayout(words_panel, BoxLayout.Y_AXIS));
        
        List<String> phraseListAll = board.getPhraseListAll();
        
        for (String s : phraseListAll) {
        	JLabel phraseLabel = new JLabel(s);
        	phraseLabel.setFont(new Font("Arial", Font.PLAIN, 35));
        	phraseLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        	phraseLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        	phraseLabel.setForeground(Color.GRAY);
        	phraseLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        	words_panel.add(phraseLabel);
        }
        
        final JScrollPane words_panel_scroll = new JScrollPane(words_panel);
        frame.add(words_panel_scroll, BorderLayout.EAST);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

   
    public static void main(String[] args) {
    	
    	SwingUtilities.invokeLater(new Game());
        
    }
}
