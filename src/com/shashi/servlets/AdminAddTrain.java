package com.shashi.servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shashi.beans.TrainBean;
import com.shashi.beans.TrainException;
import com.shashi.constant.ResponseCode;
import com.shashi.constant.UserRole;
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;
import com.shashi.utility.TrainUtil;

@WebServlet("/adminaddtrain")
public class AdminAddTrain extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminAddTrain.class.getName());

    private TrainService trainService = new TrainServiceImpl();

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();
        TrainUtil.validateUserAuthorization(req, UserRole.ADMIN);
        try {
            TrainBean train = new TrainBean();
            String trainNoStr = req.getParameter("trainno");
            String trainName = req.getParameter("trainname");
            String fromStation = req.getParameter("fromstation");
            String toStation = req.getParameter("tostation");
            String availableSeatsStr = req.getParameter("available");
            String fareStr = req.getParameter("fare");

            // Logging parameters
            LOGGER.log(Level.INFO, "Received parameters - Train No: {0}, Train Name: {1}, From Station: {2}, To Station: {3}, Available Seats: {4}, Fare: {5}",
                new Object[]{trainNoStr, trainName, fromStation, toStation, availableSeatsStr, fareStr});

            // Check for null parameters
            if (trainNoStr == null || trainName == null || fromStation == null || toStation == null || availableSeatsStr == null || fareStr == null) {
                LOGGER.log(Level.WARNING, "One or more parameters are null");
                pw.println("<div class='tab'><p1 class='menu'>Error: One or more parameters are null</p1></div>");
                return;
            }

            long trainNo = Long.parseLong(trainNoStr);
            int availableSeats = Integer.parseInt(availableSeatsStr);
            double fare = Double.parseDouble(fareStr);

            train.setTr_no(trainNo);
            train.setTr_name(trainName.toUpperCase());
            train.setFrom_stn(fromStation.toUpperCase());
            train.setTo_stn(toStation.toUpperCase());
            train.setSeats(availableSeats);
            train.setFare(fare);

            String message = trainService.addTrain(train);
            if (ResponseCode.SUCCESS.toString().equalsIgnoreCase(message)) {
                RequestDispatcher rd = req.getRequestDispatcher("AddTrains.html");
                rd.include(req, res);
                pw.println("<div class='tab'><p1 class='menu'>Train Added Successfully!</p1></div>");
            } else {
                RequestDispatcher rd = req.getRequestDispatcher("AddTrains.html");
                rd.include(req, res);
                pw.println("<div class='tab'><p1 class='menu'>Error in filling the train Detail</p1></div>");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error parsing number", e);
            pw.println("<div class='tab'><p1 class='menu'>Error: Invalid number format</p1></div>");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding train", e);
            throw new TrainException(422, this.getClass().getName() + "_FAILED", e.getMessage());
        }
    }

}
