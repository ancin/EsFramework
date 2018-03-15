package com.sishuok.es.showcase.move.web.controller;

import javax.validation.Valid;

import org.es.framework.common.entity.enums.BooleanEnum;
import org.es.framework.common.entity.validate.group.Create;
import org.es.framework.common.plugin.web.controller.BaseMovableController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.showcase.move.entity.Move;

@Controller
@RequestMapping(value = "/showcase/move")
public class MoveController extends BaseMovableController<Move, Long> {

    public MoveController() {
        setResourceIdentity("showcase:move");
    }

    @Override
    public void setCommonData(Model model) {
        model.addAttribute("booleanList", BooleanEnum.values());
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @Override
    public String create(Model model,

    @Validated(value = Create.class) @Valid Move move, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        return super.create(model, move, result, redirectAttributes);
    }

}
