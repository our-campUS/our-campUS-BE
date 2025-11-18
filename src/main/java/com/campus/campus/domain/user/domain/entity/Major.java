package com.campus.campus.domain.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Major {
	KOREAN_LITERATURE("국어국문학부(국어국문학전공)", "인문대학"),
	KOREAN_EDUCATION_AND_CULTURE("국어국문학부(한국어교육.한국문화전공)", "인문대학"),
	ENGLISH("영어영문학과", "인문대학"), GERMAN_LITERATURE("유럽문화학부(독일어문학전공)", "인문대학"),
	FRENCH_LITERATURE("유럽문화학부(프랑스어문학전공)", "인문대학"), RUSSIAN_LITERATURE("유럽문화학부(러시아어문학전공)", "인문대학"),
	CHINESE_LITERATURE("아시아문화학부(중국어문화전공)", "인문대학"), JAPANESE_LITERATURE("아시아문화학부(일본어문화전공)", "인문대학"),
	PHILOSOPHY("철학과", "인문대학"), HISTORY("역사학과", "인문대학"), POLITICAL_AND_INTERNATIONAL("정치국제학과", "사회과학대학"),
	PUBLIC_ADMINISTRATION("공공인재학부(행정학전공)", "사회과학대학"), POLICY("공공인재학부(정책학전공)", "사회과학대학"),
	PSYCHOLOGY("심리학과", "사회과학대학"), LITERATURE_AND_INFORMATICS("문헌정보학과", "사회과학대학"),
	SOCIAL_WELFARE("사회복지학부(사회복지전공)", "사회과학대학"), DIGITAL_MEDIA_CONTENT("미디어커뮤니케이션학부(디지털미디어콘텐츠전공)", "사회과학대학"),
	PRESS_INFORMATION("미디어커뮤니케이션학부(언론정보전공)", "사회과학대학"), URBAN_PLANNING_AND_REAL_ESTATE("도시계획부동산학과", "사회과학대학"),
	SOCIOLOGY("사회학과", "사회과학대학"), EDUCATION("교육학과", "사범대학"), EARLY_CHILDHOOD_EDUCATION("유아교육과", "사범대학"),
	ENGLISH_EDUCTION("영어교육과", "사범대학"), PHYSICAL_EDUCATION("체육교육과", "사범대학"), PHYSICS("물리학과", "자연과학대학"),
	CHEMISTRY("화학과", "자연과학대학"), LIFE_SCIENCE("생명과학과", "자연과학대학"), MATHEMATICS("수학과", "자연과학과"),
	ANIMAL_BIOTECHNOLOGY("생명자원공학부(다빈치)(동물생명공학전공)", "생명공학대학"), PLANT_BIOTECHNOLOGY("생명자원공학부(다빈치)(식물생명공학전공)", "생명공학대학"),
	FOOD_ENGINEERING("식품공학부(다빈치)(식품공학전공)", "생명공학대학"), FOOD_AND_NUTRITION("식품공학부(다빈치)(식품영양전공)", "생명공학대학"),
	SYSTEM_BIOTECHNOLOGY("시스템생명공학과(다빈치)", "생명공학대학"), CONSTRUCTION_ENVIRONMENT_PLANT_ENGINEERING(
		"사회기반시스템공학부(건설환경플랜트공학전공)", "공과대학"),
	URBAN_SYSTEM_ENGINEERING("사회기반시스템공학부(도시시스템공학전공)", "공과대학"), ARCHITECTURAL("건축학부(건축학전공)", "공과대학"),
	ARCHITECTURAL_ENGINEERING("건축학부(건축공학전공)", "공과대학"), CHEMICAL_ENGINEERING("화학공학과", "공과대학"),
	MECHANICAL_ENGINEERING("기계공학부", "공과대학"), NUCLEAR("에너지시스템공학부(원자력전공)", "공과대학"),
	POWER_GENERATION_MACHINERY("에너지시스템공학부(발전기계전공)", "공과대학"), ELECTRIC_POWER_GENERATION_ELECTRICAL("에너지시스템공학부(발전전기전공)",
		"공과대학"),
	ADVANCED_MATERIALS_ENGINEERING("첨단소재공학과(다빈치)", "공과대학"), ELECTRICAL_AND_ENGINEERING("전자전기공학부", "창의ICT공과대학"),
	NANOMATERIAL_ENGINEERING("융합공학부(나노소재공학전공)", "창의ICT공과대학"), BIOMEDICAL_ENGINEERING("융합공학부(바이오메디컬공학전공)", "창의ICT공학전공"),
	NEXT_GENERATION_SEMICONDUCTOR("차세대반도체학과", "창의ICT공과대학"), INTELLIGENT_SEMICONDUCTOR_ENGINEERING("지능형반도체공학과",
		"창의ICT공과대학"),
	SOFTWARE("소프트웨어학부", "소프트웨어대학"), AI("AI학과", "소프트웨어대학"), BUSINESS_ADMINISTRATION("경영학부(경영학전공)", "경영경제대학"),
	GLOBAL_FINANCE("경영학부(글로벌금융전공)", "경영경제대학"), ECONOMICS("경제학부", "경영경제대학"),
	APPLIED_STATISTICS("응용통계학과", "경영경제대학"), ADVERTISING_AND_PUBLIC_RELATIONS("광고홍보학부(광고홍보학전공)", "경영경제대학"),
	GLOBAL_ADVERTISING_PR("광고홍보학부(글로벌광고PR전공)", "경영경제대학"), KNOWLEDGE_MANAGEMENT("지식경영학부", "경영경제대학"),
	INTERNATIONAL_LOGISTICS("국제물류학과", "경영경제대학"), INDUSTRIAL_SECURITY("산업보안학과", "경영경제대학"),
	MEDICAL("의학부", "의학대학"), PHARMACY("약학부(약학전공)", "약학대학"), PHARMACEUTICAL("약학부(제약학전공)", "약학대학"),
	NURSING("간호학과", "적십자간호대학"), THEATER("공연영상창작학부(연극전공)", "예술대학"), FILM("공연영상창작학부(영화전공)", "예술대학"),
	DIRECTING_THE_PERFORMANCE_SPACE("공연영상창작학부(공연공간연출전공)", "예술대학"), LITERATURE_CREATION("공연영상창작학부(다빈치)(문예창작전공)", "예술대학"),
	PHOTOGRAPHY("공연영상창작학부(다빈치)(사진전공)", "예술대학"), DANCE("공연영상창작학부(다빈치)(무용전공)", "예술대학"),
	KOREAN_PAINTING("미술학부(다빈치)(한국화전공)", "예술대학"), WESTERN_PAINTING("미술학부(다빈치)(서양화전공)", "예술대학"),
	SCULPTURE("미술학부(다빈치)(조소전공)", "예술대학"), VISUAL_DESIGN("디자인학부(다빈치)(시각디자인전공)", "예술대학"),
	INDUSTRIAL_DESIGN("디자인학부(다빈치)(산업디자인전공)", "예술대학"), FASHION("디자인학부(다빈치)(패션전공)", "예술대학"),
	INTERIOR_ENVIRONMENTAL_DESIGN("디자인학부(다빈치)(실내환경디자인전공)", "예술대학"), CRAFT("디자인학부(다빈치)(공예전공)", "예술대학"),
	ORCHESTRAL_MUSIC("음악학부(다빈치)(관현악전공)", "예술대학"), VOCAL_MUSIC("음학학부(다빈치)(성악전공)", "예술대학"),
	COMPOSITION("음악학부(다빈치)(작곡전공)", "예숧대학"), PIANO("음악학부(다빈치)(피아노전공)", "예술대학"),
	MUSIC_AND_ARTS("전통예술학부(다빈치)(음악예술전공)", "예술대학"), PLAYED_ARTS("전통예술학부(다빈치)(연희예술전공)", "예술대학"),
	TV_BROADCASTING_ENTERTAINMENT("글로벌예술학부(다빈치)(TV방송연예전공)", "예술대학"), PRACTICAL_MUSIC("글로벌예술학부(다빈치)(실용음악전공)", "예술대학"),
	GAME_CONTENTS_ANIMATION("글로벌예술학부(다빈치)(게임콘텐츠/애니메이션전공)", "예술대학"), GAME_DEVELOP_TRACK("예술공학부(다빈치)(게임 개발 트랙)",
		"예술공학대학"),
	IMAGE_SPECIAL_EFFECTS_TRACK("예술공학부(다빈치)(영상특수효과 트랙)", "예술공학대학"), DIGITAL_ART_TRACK("예술공학부(다빈치)(디지털 아트 트랙", "예술공학대학"),
	LEISURE_AND_SPORTS("스포츠과학부(다빈치)(생활 레저스포트전공)", "체육대학"), SPORTS_INDUSTRY("스포츠과학부(다빈치)(스포츠산업전공)", "체육대학"),
	GOLF("스포츠과학부(다빈치)(골프전공)", "체육대학"), REALISTIC_MEDIA("실감미디어학과", "가상융합대학"),
	GLOBAL_INTERACTIVE_CONVERGENCE_CONTENTS("클로벌인터랙티브융합콘텐츠학과", "가상융합대학"), ADVANCED_NEW_MATERIALS("혁신소재응용공학부(첨단신소재전공)",
		"혁신융합공과대학"),
	NANO_CONVERGENCE("혁신소재응용공학부(나노융합전공)", "혁신융합공과대학"), MULTILAYER_MANUFACTURING("혁신소재응용공학부(적층제조전공)", "혁신융합공과대학"),
	INTEGRATED_MATHEMATICS("금융AI(수학과)", "융합전공"), INTEGRATED_KOREAN_AND_LITERATURE("문화콘텐츠(국어국문학과)", "융합전공"),
	INTEGRATED_BUSINESS_ONE("창업학(경영학부)", "융합전공"), INTEGRATED_BUSINESS_TWO("게임/인터렉티브미디어(경영학부)", "융합전공"),
	HUMANITIES_SOFTWARE("소프트웨어,인문(인문대학/소프트웨어학부)", "융합전공"), INTEGRATED_INDUSTRIAL_SECURITY("사이버보안(산업보안학과)", "융합전공"),
	INTEGRATED_SOFTWARE("테크놀로지아트(소프트웨어학부)", "융합전공"), INTEGRATED_BUSINESS_THREE("소프트웨어벤처(경영학부)", "융합전공"),
	INTEGRATED_BUSINESS("문화다양성(인문대학)", "융합전공"), INTEGRATED_KOREAN_AND_LITERATURE_TWO("한류문화(국어국문학과)", "융합전공");
	private final String majorName;
	private final String majorUnit;
}
