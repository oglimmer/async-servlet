package de.oglimmer.async.api.service;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.oglimmer.async.api.form.Login;

@Controller
public class Upload {

	/**
	 * Access via Browser at http://localhost:8080/fileUpload
	 */
	@RequestMapping(value = "/fileUpload", method = RequestMethod.GET)
	public String displayForm(Model model) {
		model.addAttribute("login", new Login());
		return "fileUploadForm";
	}

	/**
	 * Access via client/upload.sh http://localhost:8080/uploadFile
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) throws IOException {
		modelMap.addAttribute("file", file);
		return "fileUploadView";
	}

}
