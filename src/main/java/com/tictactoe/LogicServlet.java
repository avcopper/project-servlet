package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession(); // Получаем текущую сессию

        Field field = extractField(currentSession); // Получаем объект игрового поля из сессии

        int index = getSelectedIndex(req); // получаем индекс ячейки, по которой произошел клик
        Sign currentSign = field.getField().get(index);

        // Проверяем, что ячейка, по которой был клик пустая.
        // Иначе ничего не делаем и отправляем пользователя на ту же страницу без изменений
        // параметров в сессии
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS); // ставим крестик в ячейке, по которой кликнул пользователь

        if (checkWin(resp, currentSession, field)) { // Проверяем, не победил ли крестик после добавления последнего клика пользователя
            return;
        }

        int emptyFieldIndex = field.getEmptyFieldIndex(); // Получаем пустую ячейку поля

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
        }

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);

            if (checkWin(resp, currentSession, field)) { // Проверяем, не победил ли нолик после добавление последнего нолика
                return;
            }
        }
        else { // Если пустой ячейки нет и никто не победил - значит это ничья
            currentSession.setAttribute("draw", true); // Добавляем в сессию флаг, который сигнализирует что произошла ничья
            List<Sign> data = field.getFieldData(); // Считаем список значков
            currentSession.setAttribute("data", data); // Обновляем этот список в сессии
            resp.sendRedirect("/index.jsp"); // Шлем редирект
            return;
        }


        List<Sign> data = field.getFieldData(); // Считаем список значков


        currentSession.setAttribute("data", data); // Обновляем объект поля и список значков в сессии
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }


    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    /**
     * Метод проверяет, нет ли трех крестиков/ноликов в ряд.
     * Возвращает true/false
     */
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            currentSession.setAttribute("winner", winner); // Добавляем флаг, который показывает что кто-то победил
            List<Sign> data = field.getFieldData(); // Считаем список значков
            currentSession.setAttribute("data", data); // Обновляем этот список в сессии
            response.sendRedirect("/index.jsp"); // Шлем редирект
            return true;
        }

        return false;
    }
}
