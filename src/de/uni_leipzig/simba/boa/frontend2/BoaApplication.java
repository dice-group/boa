/**
 * 
 */
package de.uni_leipzig.simba.boa.frontend2;

import com.vaadin.Application;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class BoaApplication extends Application {

    /**
     * 
     */
    private static final long serialVersionUID = -8599745946648394242L;
    
    private TabSheet tabSheet = new TabSheet();
    
    // Icons for the table
    private static final ThemeResource icon1 = new ThemeResource("icons/32/globe.png");
    private static final ThemeResource icon2 = new ThemeResource("icons/32/globe.png");
    private static final ThemeResource icon3 = new ThemeResource("icons/32/globe.png");

    /**
     * 
     */
    @Override
    public void init() {
        
        // Tab 1 content
        VerticalLayout l1 = new VerticalLayout();
        l1.setMargin(true);
        l1.addComponent(new Label("There are no previously saved actions."));
        // Tab 2 content
        VerticalLayout l2 = new VerticalLayout();
        l2.setMargin(true);
        l2.addComponent(new Label("There are no saved notes."));
        // Tab 3 content
        VerticalLayout l3 = new VerticalLayout();
        l3.setMargin(true);
        l3.addComponent(new Label("There are currently no issues."));
        // Tab 3 content
        VerticalLayout l4 = new VerticalLayout();
        l4.setMargin(true);
        l4.addComponent(new Label("There are no downloads atm."));
        
        VerticalLayout l5 = new VerticalLayout();
        l5.setMargin(true);
        l5.addComponent(new TextField("Filter"));

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.addTab(l1, "Pattern Library", icon1).setStyleName("pattern_library");
        tabSheet.addTab(l2, "Generated RDF", icon2).setStyleName("generated_rdf");
        tabSheet.addTab(l3, "Downloads", icon3).setStyleName("downloads");
        tabSheet.addTab(l4, "More", icon3).setStyleName("more");
        tabSheet.addTab(l5, "Search", icon3).setStyleName("more");
        
//        HorizontalLayout toolbar = new HorizontalLayout();
        final VerticalLayout layout = new VerticalLayout();
        layout.setWidth("85%");
        layout.setHeight("85%");
        layout.addComponent(tabSheet);
        layout.setComponentAlignment(tabSheet, Alignment.MIDDLE_CENTER);

        final Window mainWindow = new Window("Boa Frontend");
        mainWindow.setSizeFull();
        mainWindow.center();
        setMainWindow(mainWindow);
        
        this.setTheme("boa2");
        this.getMainWindow().setContent(layout);
    }
}
