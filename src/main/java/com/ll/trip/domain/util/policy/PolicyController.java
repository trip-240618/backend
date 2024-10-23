package com.ll.trip.domain.util.policy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy")
public class PolicyController {
	@GetMapping("/privacy")
	public String showPrivate() {
		return "redirect:/privacy.html";
	}

	@GetMapping("/marketing")
	public String showMarketing() {
		return "redirect:/marketing.html";
	}

	@GetMapping("/service")
	public String showService() {
		return "redirect:/service.html";
	}

	@GetMapping("/location")
	public String showLocation() {
		return "redirect:/location.html";
	}

	@GetMapping("/offer")
	public String showOffer() {
		return "redirect:/offer.html";
	}
}
