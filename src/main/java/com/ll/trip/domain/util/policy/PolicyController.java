package com.ll.trip.domain.util.policy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/policy")
@Tag(name = "TermsAndPolicy")
public class PolicyController {
	@GetMapping("/privacy")
	@Operation(summary = "개인정보처리방침")
	public String showPrivate() {
		return "redirect:/policy/privacy.html";
	}

	@GetMapping("/marketing")
	@Operation(summary = "마케팅-수신-동의")
	public String showMarketing() {
		return "redirect:/policy/marketing.html";
	}

	@GetMapping("/service")
	@Operation(summary = "서비스-이용약관")
	public String showService() {
		return "redirect:/policy/service.html";
	}

	@GetMapping("/location")
	@Operation(summary = "위치정보-이용약관")
	public String showLocation() {
		return "redirect:/policy/location.html";
	}

	@GetMapping("/offer")
	@Operation(summary = "제3자-정보제공-동의")
	public String showOffer() {
		return "redirect:/policy/offer.html";
	}
}
