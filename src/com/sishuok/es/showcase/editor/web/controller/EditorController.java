package com.sishuok.es.showcase.editor.web.controller;

import org.es.framework.common.web.controller.BaseCRUDController;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sishuok.es.showcase.editor.entity.Editor;

@Controller
@RequestMapping(value = "/showcase/editor")
public class EditorController extends BaseCRUDController<Editor, Long> {

    public EditorController() {
        setResourceIdentity("showcase:editor");
    }

    @Override
    protected boolean hasError(Editor m, BindingResult result) {
        Assert.notNull(m);

        return result.hasErrors();
    }

}
