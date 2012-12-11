package dm.view;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User:
 * Date: 10.12.12
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame{
    public MainFrame(mxGraph graph) throws HeadlessException {
        super("hello");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);

        layout.setMinDistanceLimit(50);
        layout.setMaxDistanceLimit(100);
        layout.setInitialTemp(100);
        layout.setForceConstant(100);
        layout.setDisableEdgeStyle(true);

        layout.execute(graph.getDefaultParent());

        mxGraphComponent mxgc = new mxGraphComponent(graph);
        add(mxgc);


        setVisible(true);
    }
}
