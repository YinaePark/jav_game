package command;

import javax.swing.*;
import java.awt.*;

public class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        // 도움말 다이얼로그 생성
        JDialog helpDialog = new JDialog();
        helpDialog.setTitle("Game Help");
        helpDialog.setSize(400, 300);
        helpDialog.setLocationRelativeTo(null);
        helpDialog.setModal(true);
        
        // 도움말 내용을 담을 텍스트 영역
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setMargin(new Insets(10, 10, 10, 10));
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // 도움말 내용
        StringBuilder help = new StringBuilder();
        help.append("Game Controls:\n\n");
        help.append("Mouse Controls:\n");
        help.append("• Left Click - Till the soil\n");
        help.append("• Right Click - Plant a crop\n\n");
        help.append("Keyboard Controls:\n");
        help.append("• WASD - Move the player\n");
        help.append("• H - Show this help\n");
        help.append("• I - Show inventory\n");
        help.append("• F - Show farm status\n");
        help.append("• SPACE - Harvest crops\n");
        help.append("• Q - Quit game\n\n");
        help.append("Game Tips:\n");
        help.append("1. Till the soil before planting\n");
        help.append("2. Make sure you have enough money to plant crops\n");
        help.append("3. Wait for crops to grow before harvesting");
        
        helpText.setText(help.toString());
        
        // 스크롤 패널에 텍스트 영역 추가
        JScrollPane scrollPane = new JScrollPane(helpText);
        helpDialog.add(scrollPane);
        
        // 닫기 버튼
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> helpDialog.dispose());
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // 다이얼로그 표시
        helpDialog.setVisible(true);
    }
}