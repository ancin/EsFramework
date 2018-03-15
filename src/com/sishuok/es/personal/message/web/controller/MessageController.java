package com.sishuok.es.personal.message.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.es.framework.common.Constants;
import org.es.framework.common.web.bind.annotation.PageableDefaults;
import org.es.framework.common.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sishuok.es.personal.message.entity.Message;
import com.sishuok.es.personal.message.entity.MessageContent;
import com.sishuok.es.personal.message.entity.MessageState;
import com.sishuok.es.personal.message.service.MessageApi;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserService;
import com.sishuok.es.sys.user.web.bind.annotation.CurrentUser;

@Controller
@RequestMapping("/admin/personal/message")
public class MessageController extends BaseController<Message, Long> {
    @Autowired
    private MessageApi  messageApi;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String listDefault(@CurrentUser User user, Pageable pageable, Model model) {
        return list(user, MessageState.in_box, pageable, model);
    }

    @RequestMapping(value = "{state}/list", method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public String list(@CurrentUser User user, @PathVariable("state") MessageState state,
                       Pageable pageable, Model model) {

        model.addAttribute("state", state);
        model.addAttribute("page", messageApi.findUserMessage(user.getId(), state, pageable));
        model.addAttribute("states", MessageState.values());

        return viewName("list");
    }

    /**
     * �����ر������
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTableDefault(@CurrentUser User user, Pageable pageable, Model model) {
        list(user, MessageState.in_box, pageable, model);
        return viewName("listTable");
    }

    @RequestMapping(value = "{state}/list", method = RequestMethod.GET, headers = "table=true")
    @PageableDefaults(sort = "id=desc")
    public String listTable(@CurrentUser User user, @PathVariable("state") MessageState state,
                            Pageable pageable, Model model) {
        list(user, state, pageable, model);
        return viewName("listTable");
    }

    @RequestMapping("{m}")
    public String view(@CurrentUser User user, @PathVariable("m") Message m, Model model,
                       RedirectAttributes redirectAttributes) {
        if (m == null) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, "���鿴����Ϣ������");
            return redirectToUrl("list");
        }

        if (user.getId().equals(m.getReceiverId())) {
            messageApi.markRead(m);
        }

        //�õ���Ϣ֮ǰ�� �� ֮���
        List<Message> messages = messageApi.findAncestorsAndDescendants(m);
        model.addAttribute("messages", messages);

        return viewName("view");
    }

    @RequestMapping("{m}/content")
    public String viewContent(@CurrentUser User user, @PathVariable("m") Message m, Model model,
                              RedirectAttributes redirectAttributes) {

        if (user.getId().equals(m.getReceiverId())) {
            messageApi.markRead(m);
        }

        return viewName("viewContent");
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String showSendForm(Model model) {
        if (!model.containsAttribute("m")) {
            model.addAttribute("m", newModel());
        }
        model.addAttribute(Constants.OP_NAME, "��������Ϣ");
        return viewName("sendForm");
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send(@CurrentUser User user, @Valid @ModelAttribute("m") Message message,
                       BindingResult result,
                       @RequestParam(value = "receiver", required = false) String receiverUsername,
                       Model model, RedirectAttributes redirectAttributes) {

        User receiver = userService.findByUsername(receiverUsername);
        if (receiver == null) {
            result.rejectValue("receiverId", "receiver.not.exists");
        }
        if (receiver.equals(user)) {
            result.rejectValue("receiverId", "receiver.not.self");
        }

        if (result.hasErrors()) {
            return showSendForm(model);
        }
        message.setReceiverId(receiver.getId());
        message.setSenderId(user.getId());
        messageApi.send(message);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "���ͳɹ���");
        return redirectToUrl(viewName(MessageState.out_box + "/list"));
    }

    @RequestMapping(value = "/{parent}/reply", method = RequestMethod.GET)
    public String showReplyForm(@PathVariable("parent") Message parent, Model model) {
        if (!model.containsAttribute("m")) {
            Message m = newModel();
            m.setParentId(parent.getId());
            m.setParentIds(parent.getParentIds());
            m.setReceiverId(parent.getSenderId());
            m.setTitle(MessageApi.REPLY_PREFIX + parent.getTitle());
            model.addAttribute("m", m);
        }
        model.addAttribute(Constants.OP_NAME, "�ظ���Ϣ");
        return viewName("sendForm");
    }

    @RequestMapping(value = "/{parent}/reply", method = RequestMethod.POST)
    public String reply(@CurrentUser User user, @PathVariable("parent") Message parent,
                        @ModelAttribute("m") Message m, BindingResult result,
                        RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            return showReplyForm(parent, model);
        }
        m.setSenderId(user.getId());
        messageApi.send(m);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�ظ��ɹ���");
        return redirectToUrl(viewName(MessageState.out_box + "/list"));
    }

    @RequestMapping(value = "/{parent}/forward", method = RequestMethod.GET)
    public String showForwardForm(@PathVariable("parent") Message parent, Model model) {

        String receiverUsername = userService.findOne(parent.getReceiverId()).getUsername();
        String senderUsername = userService.findOne(parent.getSenderId()).getUsername();

        if (!model.containsAttribute("m")) {
            Message m = newModel();
            m.setTitle(MessageApi.FOWRARD_PREFIX + parent.getTitle());
            m.setContent(new MessageContent());
            m.getContent().setContent(
                String.format(MessageApi.FOWRARD_TEMPLATE, senderUsername, receiverUsername,
                    parent.getTitle(), parent.getContent().getContent()));
            model.addAttribute("m", m);
        }
        model.addAttribute(Constants.OP_NAME, "ת����Ϣ");
        return viewName("sendForm");
    }

    @RequestMapping(value = "/{parent}/forward", method = RequestMethod.POST)
    public String forward(@CurrentUser User user,
                          @RequestParam(value = "username", required = false) String username,
                          @PathVariable("parent") Message parent, @ModelAttribute("m") Message m,
                          BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        User receiver = userService.findByUsername(username);
        if (receiver == null) {
            result.rejectValue("receiverId", "receiver.not.exists");
        }

        if (receiver.equals(user)) {
            result.rejectValue("receiverId", "receiver.not.self");
        }

        if (result.hasErrors()) {
            return showForwardForm(parent, model);
        }
        m.setReceiverId(receiver.getId());
        m.setSenderId(user.getId());
        messageApi.send(m);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "ת���ɹ���");
        return redirectToUrl(viewName(MessageState.out_box + "/list"));
    }

    @RequestMapping(value = "draft/save", method = RequestMethod.POST)
    public String saveDraft(@CurrentUser User user,
                            @RequestParam(value = "username", required = false) String username,
                            @ModelAttribute("m") Message m, RedirectAttributes redirectAttributes) {

        User receiver = userService.findByUsername(username);
        if (receiver != null) {
            m.setReceiverId(receiver.getId());
        }
        m.setSenderId(user.getId());

        messageApi.saveDraft(m);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "����ݸ�ɹ���");
        return redirectToUrl(viewName(MessageState.draft_box + "/list"));
    }

    @RequestMapping(value = "draft/{m}/send", method = RequestMethod.GET)
    public String showResendDraftForm(@PathVariable("m") Message m, Model model) {
        if (m.getReceiverId() != null) {
            User user = userService.findOne(m.getReceiverId());
            if (user != null) {
                model.addAttribute("username", user.getUsername());
            }
        }
        model.addAttribute("m", m);
        String viewName = showSendForm(model);
        model.addAttribute(Constants.OP_NAME, "���Ͳݸ�");
        return viewName;
    }

    @RequestMapping(value = "draft/{m}/send", method = RequestMethod.POST)
    public String resendDraft(@CurrentUser User user, @Valid @ModelAttribute("m") Message m,
                              BindingResult result,
                              @RequestParam(value = "username", required = false) String username,
                              Model model, RedirectAttributes redirectAttributes) {

        String viewName = send(user, m, result, username, model, redirectAttributes);
        model.addAttribute(Constants.OP_NAME, "���Ͳݸ�");
        return viewName;
    }

    @RequestMapping("batch/store")
    public String batchStore(@CurrentUser User user,
                             @RequestParam(value = "ids", required = false) Long[] ids,
                             RedirectAttributes redirectAttributes) {

        messageApi.store(user.getId(), ids);
        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�ղسɹ���");
        return redirectToUrl(viewName(MessageState.store_box + "/list"));
    }

    @RequestMapping("batch/recycle")
    public String batchRecycle(@CurrentUser User user,
                               @RequestParam(value = "ids", required = false) Long[] ids,
                               RedirectAttributes redirectAttributes) {

        messageApi.recycle(user.getId(), ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�ƶ���������ɹ���");
        return redirectToUrl(viewName(MessageState.trash_box + "/list"));
    }

    @RequestMapping("batch/delete")
    public String batchDelete(@CurrentUser User user,
                              @RequestParam(value = "ids", required = false) Long[] ids,
                              RedirectAttributes redirectAttributes) {

        messageApi.delete(user.getId(), ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "ɾ���ɹ���");
        return redirectToUrl(viewName(MessageState.trash_box + "/list"));
    }

    @RequestMapping("clear/{state}")
    public String clear(@CurrentUser User user, @PathVariable("state") MessageState state,
                        RedirectAttributes redirectAttributes) {

        messageApi.clearBox(user.getId(), state);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE,
            String.format("���%s�ɹ���", state.getInfo()));
        return redirectToUrl(viewName(MessageState.trash_box + "/list"));
    }

    @RequestMapping("markRead")
    public String markRead(@CurrentUser User user,
                           @RequestParam(value = "ids", required = false) Long[] ids,
                           @RequestParam("BackURL") String backURL,
                           RedirectAttributes redirectAttributes) {

        messageApi.markRead(user.getId(), ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGE, "�ɹ����Ϊ�Ѷ���");
        return redirectToUrl(backURL);

    }

    @RequestMapping(value = "/unreadCount")
    @ResponseBody
    public String unreadCount(@CurrentUser User user) {
        return String.valueOf(messageApi.countUnread(user.getId()));
    }

}
