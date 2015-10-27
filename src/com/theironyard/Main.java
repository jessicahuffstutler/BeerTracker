package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Beer> beers = new ArrayList();

        Spark.get(
            "/",
            ((request, response) -> {
                Session session = request.session();
                String username = session.attribute("username");
                if (username == null) {
                    return new ModelAndView(new HashMap(), "not-logged-in.html"); //return non logged in one
                }
                HashMap m = new HashMap();
                m.put("username", username);
                m.put("beers", beers);
                return new ModelAndView(m, "logged-in.html");
            }),
            new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");  //pull the parameter from the HTML form
                    Session session = request.session();
                    session.attribute("username", username); //pull the parameter from the HTML form using username from above, the first "username" is from the HTML page
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/create-beer",
                ((request, response) -> {
                    Beer beer = new Beer();
                    beer.id = beers.size() + 1; //it'll start at 0 and then add 1 to show as 1, 2, 3, etc.
                    beer.name = request.queryParams("beername");
                    beer.type = request.queryParams("beertype");
                    beers.add(beer);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/delete-beer",
                ((request, response) -> {
                    String id = request.queryParams("beerid");
                    try {
                        int idNum = Integer.valueOf(id);
                        beers.remove(idNum - 1);
                        for (int i = 0; i < beers.size(); i++) { //renumbering the beers after you delete one of them
                            beers.get(i).id = i + 1; //renumbering the beers after you delete one of them
                        }
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );
    }
}
