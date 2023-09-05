package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession(true); // Создание новой сессии

        Field field = new Field(); // Создание игрового поля
        Map<Integer, Sign> fieldData = field.getField();
        List<Sign> data = field.getFieldData(); // Получение списка значений поля

        currentSession.setAttribute("field", field); // Добавление в сессию параметров поля (нужно будет для хранения состояния между запросами)
        currentSession.setAttribute("data", data); // и значений поля, отсортированных по индексу (нужно для отрисовки крестиков и ноликов)
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp); // Перенаправление запроса на страницу index.jsp через сервер
    }
}
