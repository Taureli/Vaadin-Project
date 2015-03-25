package com.jakub.vaadinprojekt;

import java.awt.TextField;

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
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
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

	//First layout
    final VerticalLayout loginLayout = new VerticalLayout();
    
	//Second layout
    final VerticalLayout lobbyLayout = new VerticalLayout();
    
	@Override
    protected void init(VaadinRequest vaadinRequest) {

		loginLayout.setMargin(true);
        lobbyLayout.setMargin(true);
        
        item.setItemDataSource(person);
        loginLayout.addComponent(prepareForm());        
        
        //First button
        Button testBtn = new Button("Wyloguj");
        testBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent event) {
                setContent(loginLayout);
            }
        });
        lobbyLayout.addComponent(testBtn);

        //Setting content - login screen if not logged, else - lobby screen
        if(VaadinSession.getCurrent().getSession().getAttribute("user") == null)
        	setContent(loginLayout);
        else{
        	lobbyLayout.addComponent(new Label("user: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
        	setContent(lobbyLayout);
        }
    }
	
	//creating login button
	public Button loginButton(){
		Button button = new Button("Zaloguj");
		button.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					item.commit();
					
					//set username in session
					VaadinSession.getCurrent().getSession().setAttribute("user", person.getNickname());
					lobbyLayout.addComponent(new Label("user: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
					setContent(lobbyLayout);
					
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
		
		form.addComponent(loginButton());
		return form;
		}

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;
    }
    
}
