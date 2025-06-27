package com.crudApp;

import com.crudApp.controller.LabelController;
import com.crudApp.controller.PostController;
import com.crudApp.controller.WriterController;
import com.crudApp.repository.impl.JdbcLabelRepositoryImpl;
import com.crudApp.repository.impl.JdbcPostRepositoryImpl;
import com.crudApp.repository.impl.JdbcWriterRepositoryImpl;
import com.crudApp.service.impl.LabelServiceImpl;
import com.crudApp.service.impl.PostServiceImpl;
import com.crudApp.service.impl.WriterServiceImpl;
import com.crudApp.view.ConsoleView;
import com.crudApp.view.LabelView;
import com.crudApp.view.PostView;
import com.crudApp.view.WriterView;

public class Main {
    public static void main(String[] args) {
        var labelRepository = new JdbcLabelRepositoryImpl();
        var postRepository = new JdbcPostRepositoryImpl();
        var writerRepository = new JdbcWriterRepositoryImpl();

        var labelService = new LabelServiceImpl(labelRepository);
        var postService = new PostServiceImpl(postRepository, labelRepository);
        var writerService = new WriterServiceImpl(writerRepository);

        var labelController = new LabelController(labelService);
        var postController = new PostController(postService);
        var writerController = new WriterController(writerService);

        var labelView = new LabelView(labelController);
        var postView = new PostView(postController);
        var writerView = new WriterView(writerController);

        var consoleView = new ConsoleView(labelView, postView, writerView);
        consoleView.start();

    }
}