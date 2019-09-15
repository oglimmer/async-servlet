package de.oglimmer.async.api.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.oglimmer.async.api.component.ThreadStats;
import de.oglimmer.async.api.form.Login;

@Controller
public class Upload {

	@Autowired
	private ThreadStats threadStats;
	
	@RequestMapping(value = "/fileUpload", method = RequestMethod.GET)
	public String displayForm(Model model) {
		model.addAttribute("login", new Login());
		return "fileUploadForm";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) throws IOException {
		int id = threadStats.incActive();
		modelMap.addAttribute("file", file);
		threadStats.decActive(id);
		threadStats.incAll(1, file.getBytes().length);
		return "fileUploadView";
	}

}
