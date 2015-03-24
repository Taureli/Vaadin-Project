package com.jakub.vaadinprojekt;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@Widgetset("com.jakub.vaadinprojekt.MyAppWidgetset")
public class MyUI extends UI {

	private static final long serialVersionUID = 1L;

	@Override
    protected void init(VaadinRequest vaadinRequest) {

		//First layout
        final VerticalLayout testLayout = new VerticalLayout();
        Button testBtn = new Button("hehe");
        
        //Second layout
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        
        //First button
        testBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent event) {
                setContent(layout);
            }
        });
        testLayout.addComponent(testBtn);
        
        //Second button
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
                setContent(testLayout);
            }
        });
        layout.addComponent(button);

        //Setting content
        setContent(testLayout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;
    }
}
