
import com.actelion.research.chem.ExtendedDepictor;
import com.actelion.research.gui.JEditableChemistryView;
import com.actelion.research.gui.JEditableStructureView;
import com.actelion.research.gui.clipboard.ClipboardHandler;

import javax.swing.*;
import java.awt.*;

/*
 *  Manual Test Dialog to test c&p between OCL editors and ChemDraw
 */
public class TestDialog {

    public static void main(String[] args) {
        JFrame f = new JFrame("ClipboardHandler Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panely = new JPanel();
        panely.setLayout(new BoxLayout(panely, BoxLayout.Y_AXIS));
        f.add(panely);

        JPanel panelx = new JPanel();
        panelx.setLayout(new BoxLayout(panelx, BoxLayout.X_AXIS));

        JPanel panelx2 = new JPanel();
        panelx2.setLayout(new BoxLayout(panelx2, BoxLayout.X_AXIS));

        JEditableChemistryView natMol = new JEditableChemistryView(ExtendedDepictor.TYPE_MOLECULES);
        JEditableChemistryView natRxn = new JEditableChemistryView(ExtendedDepictor.TYPE_REACTION);
        JEditableStructureView natStruct = new JEditableStructureView();
        natStruct.setClipboardHandler(new ClipboardHandler());

        panelx.add(natMol);
        panelx.add(natStruct);
        panelx.add(natRxn);



        String toggle = "Toggle Class: ";
        JButton toggleButton = new JButton(toggle + getCurrentNativeClipClass());
        toggleButton.addActionListener(a -> {
            ClipboardHandler.useNextnativeCliphandler(false);
            toggleButton.setText(toggle + getCurrentNativeClipClass());
        });
        /*JButton sizedMolButton = new JButton("CopySizedMolecule");
        JTextField widthTextField = new JTextField("Width");
        JTextField heightTextField = new JTextField("Height");
        sizedMolButton.addActionListener(a-> {
            new ClipboardHandler().copySizedMolecule(natMol.getStructures()[0], Integer.valueOf(widthTextField.getText()), Integer.valueOf(heightTextField.getText()));
        });*/


        panelx2.add(toggleButton);
        /*panelx2.add(sizedMolButton);
        panelx2.add(widthTextField);
        panelx2.add(heightTextField);*/
        panelx2.setMaximumSize(new Dimension(panelx2.getMaximumSize().width, 40));

        panely.add(panelx);
        panely.add(panelx2);
        //panely.add(toggleButton2);

        f.setSize(new Dimension(800, 800));
        f.setVisible(true);

    }
    public static String getCurrentNativeClipClass() {
        if (ClipboardHandler.getNativeCliphandlerList() != null && ClipboardHandler.getNativeCliphandlerList().size() > 0) {
            String clz = ClipboardHandler.getNativeCliphandlerList().get(0);
            return clz.substring(clz.lastIndexOf(".") + 1);
        }
        return null;
    }
}
