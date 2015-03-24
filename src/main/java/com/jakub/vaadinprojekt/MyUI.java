package com.jakub.vaadinprojekt;

import javax.servlet.annotation.WebServlet;

import com.jakub.vaadinprojekt.domain.Person;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("mytheme")
@Widgetset("com.jakub.vaadinprojekt.MyAppWidgetset")
public class MyUI extends UI {

	private static final long serialVersionUID = 1L;

	Person person = new Person();
	BeanFieldGroup<Person> item = new BeanFieldGroup<Person>(Person.class);

	@Override
    protected void init(VaadinRequest vaadinRequest) {

		//First layout
        final VerticalLayout testLayout = new VerticalLayout();
        Button testBtn = new Button("hehe");
        
        //Second layout
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        
        item.setItemDataSource(person);
        testLayout.addComponent(prepareForm());
        
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
	
	public Button makeSendButton(){
		Button button = new Button("Send");
		button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					item.commit();

					//TEMPORARY POP-UP WITH USERNAME
					final Window window = new Window("Data");
					window.setWidth(300, Unit.PIXELS);

					final VerticalLayout windowLayout = new VerticalLayout();
					windowLayout.setMargin(true);
					windowLayout.addComponent(new Label("Name: " + person.getNickname()));
					window.setContent(windowLayout);

					UI.getCurrent().addWindow(window);
					
				} catch (CommitException e) {
					e.printStackTrace();
				}
			}
		});
		return button;
	}
	
	public FormLayout prepareForm(){
		FormLayout form = new FormLayout();
		
		AbstractTextField name = (AbstractTextField) item.buildAndBind("Nickname", "nickname");
		name.setNullRepresentation("");
		form.addComponent(name);
		
//		AbstractTextField pass = (AbstractTextField) item.buildAndBind("Password", "password");
//		pass.setNullRepresentation("");
//		form.addComponent(pass);
//		
//		AbstractTextField passTest = (AbstractTextField) item.buildAndBind("Confirm password", "passwordTest");
//		passTest.setNullRepresentation("");
//		form.addComponent(passTest);
		
		form.addComponent(makeSendButton());
		return form;
		}

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;
    }
}
