package com.hotelnow.utils;

import com.hotelnow.BuildConfig;
import com.thebrownarrow.model.SearchResultItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by susia on 15. 9. 17..
 */

public class CONFIG {
    // 과거 dev server : 54.238.211.153 // sync->dev server 54.64.90.137
    public final static String domain = BuildConfig.domain;                                 //	api url
    public final static String domain2 = BuildConfig.domain2;                                 //	api url
    public final static String domainssl = BuildConfig.domainssl;                           //	ssl url
    public final static String arsDomain = BuildConfig.arsDomain;                           //	ars url
    public final static String billDomain = BuildConfig.billDomain;                         //	pay url
    public final static String phonePayDomain = BuildConfig.phonePayDomain;                 //	phone pay url
    public final static String cardDomain = BuildConfig.cardDomain;                 //	phone pay url
    public final static String googleProjectid = BuildConfig.googleProjectid;               //	구글 프로젝트 id
    public final static String KcpCardDomain = BuildConfig.kcpCardDomain;                   //  kcp url
    public final static String homeWebUrl = BuildConfig.webUrl;

    public final static String loadingUrl = domain + "/config";                                //	앱 실행 가능 여부 확인
    public final static String authcheckUrl = domain + "/auth_checking";                        //	로그인 확인
    public final static String arsIndex = arsDomain + "/order.php";                            //	빌링 index url
    public final static String billIndex = billDomain + "/approval.php";                        //	빌링 index url
    public final static String phonePayIndex = phonePayDomain + "/mc_web.php";                //	폰빌링 index url
    public final static String cardPayIndex = cardDomain + "/check.php";                    //	간편결제 index url

    public final static String bookingReserveUrl = domain + "/booking/reservation";            //	카드, 무통장 결제 시작.
    public final static String bookingQReserveUrl = domain + "/q/booking_reservation";        //  티켓- 카드, 무통장 결제 시작.
    public final static String bookingSuccessUrl = domain + "/booking/confirm";                //	카드 결제 완료.
    public final static String bookingCancelUrl = domain + "/booking/cancel";                //	카드 결제 취소.

    public final static String mainListUrl = domain + "/products_new";                        //	param1 : page_num , param2 : count / 리스트
    public final static String mainListUrl_v2 = domain + "/products_v2";                        //	param1 : page_num , param2 : count / 리스트
    public final static String areaCntUrl = domain + "/product/area_cnt";                        //	지역별 판매 호텔 수
    public final static String detailUrl = domain + "/product_detail_v2";                        //	param 1 : deal_id / 상품 상세
    public final static String ticketdetailUrl = domain + "/q/deal_detail";                    //	티켓 상품 상세
    public final static String ticketreserveUrl = domain + "/q/booking_ready";                //	티켓 상품 예약
    public final static String couponUrl = domain + "/promotion/get_instant_coupon";                        //	param 1 : hotel_id / 쿠폰
    public final static String portraitUrl = domain + "/product_big_image";                    //	전체화면 이미지 갤러리 정보
    public final static String reserveUrl = domain + "/product/available";                    //	예약전 예약 가능 상태 확인
    public final static String detailWebUrl = domain + "/product_detail_web";                    //	상품 정보 웹페이지
    public final static String locationUrl = domain + "/product/location";                    //	호텔 위치정보
    public final static String locationNearUrl = domain + "/product/near_hotel";                //	호텔 위치 주변 정보
    public final static String checkinDateUrl = domain + "/product/avail_dates";                //	호텔 투숙 가능일 정보

    public final static String themeMainUrl = domain + "/theme";                                //	호텔 위치 주변 정보
    public final static String themeHotelUrl = domain + "/theme/hotels";                        //	호텔 위치 주변 정보

    public final static String captchaUrl = domain + "/captcha";                                //	captcha 이미지 주소
    public final static String agreeUrl = domain + "/terms";                                    //	param 약관
    public final static String loginUrl = domainssl + "/login";                                //	login
    public final static String logoutUrl = domainssl + "/logout";                                //	logout
    public final static String signupUrl = domainssl + "/signup";                                //	signup
    public final static String signoutUrl = domainssl + "/signout";                            //	signout
    public final static String passresetUrl = domainssl + "/password/remind";                    //	비번 초기화
    public final static String retireinfoUrl = domainssl + "/retire_info";                           //	탈퇴 정보
    public final static String retirepwUrl = domainssl + "/check_password";                      //	탈퇴 비밀번호 입력
    public final static String retireUrl = domainssl + "/retire";                            //	탈퇴 완료

    public final static String bookingListUrl = domain + "/api/booking";                        //	예약리스트 / booking/list/{page}/{per_page}
    public final static String bookingDetailUrl = domain + "/api/booking";                    //	예약상세 / booking/detail/{booking_id}
    public final static String eventApplyUrl = domain + "/booking/event";                        //	이벤트 응모하기
    public final static String bookingHidelUrl = domain + "/booking/hide";                    //	예약내역 숨김
    public final static String bookingReceiptlUrl = domain + "/booking/receipt";                //	영수증 발급

    public final static String reviewCreateUrl = domain + "/review/create";                    //	리뷰쓰기
    public final static String reviewCreateUrl_v2 = domain + "/review/create_v2";                    //	리뷰쓰기
    public final static String reviewListUrl = domain + "/review/list_v2";                    //	리뷰리스트 param1 : page_num , param2 : count , param3 : hotel
    public final static String qreviewListUrl = domain + "/q/review/list_v2";                    //	리뷰리스트 param1 : page_num , param2 : count , param3 : hotel
    public final static String eventWebUrl = domain2 + "/popup_event";                            //	이벤트 웹 url

    public final static String ticketUrl = domain + "/activity";                           //  티켓리스트
    public final static String ticket_booking_Url = domain + "/api/qbooking";                 //  티켓예약리스트
    public final static String ticketbookingSuccessUrl = domain + "/q/booking_confirm";       //  티켓예약완료
    public final static String ticketbookingDetailUrl = domain + "/api/qbooking";              //  티켓예약상세
    public final static String themeTicketUrl = domain + "/theme/ticket";                        //	테마 티켓
    public final static String ticketreviewCreateUrl_v2 = domain + "/q/review/create_v2";
    public final static String ticketreviewUrl_v2 = domain + "/q/review/list_v2";
    public final static String bookingticketHidelUrl = domain + "/q/booking_hide";                    //	예약내역 숨김
    public final static String bookingticketReceiptlUrl = domain + "/q/booking_receipt";                //	영수증 발급
    public final static String ticketcouponUrl = domain + "/coupon/instant/activity";                //	티켓 쿠폰 리스트
    public final static String ticketcouponUrl2 = domain + "/coupon/receive_instant/activity";                //	티켓 쿠폰 조회

    public final static String notiSettingUrl = domain + "/notification/setting";                //	notification 설정하기
    public final static String notiStatusUrl = domain + "/notification/status";                //	notification 설정 값 확인

    public final static String setting_agree1 = domain + "/terms/service";                    //	이용약관
    public final static String setting_agree2 = "http://m.policy.yanolja.com/?t=privacy&d=m";                    //	개인정보 취급방침
    public final static String setting_agree3 = "http://m.policy.yanolja.com/?t=location&d=m";                    //	개인정보 취급방침
    public final static String setting_agree4 = "http://www.ftc.go.kr/bizCommPop.do?wrkr_no=2208742885&apv_perm_no=";                    //	개인정보 취급방침
    public final static String info_provide1 = domain + "/terms_booking/booking";                //	구매 주의사항
    public final static String info_provide2 = domain + "/terms_booking/thirdparty";            //	개인정보 제 3자 동의
    public final static String info_provide3 = domain + "/terms_booking/qbooking";                //	구매 주의사항

    public final static String reservemoneyUrl = domain + "/recommendation/list";                //	적립금
    public final static String recommendSaveUrl = domain + "/recommendation/save";            //	적립금
    public final static String recommendCheckUrl = domain + "/recommendation/check";            //	적립금
    public final static String recommendInfoUrl = domain + "/recommendation/get_save_money";    //	적립금, 쿠폰 정보

    public final static String promotionListUrl = domain + "/coupon/lists";                    //	프로모션 리스트
    public final static String promotionUrl = domain + "/promotion/receive";                    //	프로모션
    public final static String promotionUrl2 = domain + "/promotion/receive_instant";            //	프로모션
    public final static String promotionHotelsUrl = domain + "/hotel/get_names";                    //	호텔리스트 내리도록

    public final static String cardManageUrl = domain + "/card/manage";                        //	신용카드 관리
    public final static String cardRemoveUrl = domain + "/card/remove";                        //	신용카드 삭제
    public final static String cardAddUrl = cardDomain + "/cardadd.php";                        //	신용카드 추가

    public final static String privateDeaUrl = domain + "/wave/privatedeal_init";         //  프라이빗 연동
    public final static String privateDeaProposalUrl = domain + "/wave/privatedeal_proposal";         //  프라이빗 제안

    public final static String marketUrl = "http://api.hotelnow.co.kr/install_track?hotel_id=&s_date=&e_date=&is_event=N&evt_id=&t_id=";                //	marketurl
    public final static String phone_auth = domain + "/phone_auth/request"; // 번호 인증 요청
    public final static String phone_auth_check = domain + "/phone_auth/check"; // 번호 인증 확인


    public final static String setting_facebook = "https://www.facebook.com/hotelnowkr";    //	facebook
    public final static String setting_notice = domain + "/board/notice/view/ko";             //  공지사항
    public final static String setting_faq = domain + "/board/faq/view/ko";                    //	자주묻는질문
    public final static String setting_email = "hotelnow@yanolja.com";                            //	email 주소

    public final static String fontPath = "fonts/nanumgothic.ttf";                            //	폰트경로
    public final static String accountNumber = "우리은행 1005-302-209367";                        //	계좌번호
    public final static String csPhoneNum = "1670-6864";                                    //	cs 전화번호
    public final static String newrelicKey = "AAb598d6751668162b9954ab68b628a549d1b3d0e7";    //	newrelic 키
    public final static String kimgisaKey = "c1be8498949d40bdb9b9fa38acd54737";                //	카카오내비 키
    public static String PrivateUrl = "";                                                   // 프라이빗딜 url
    public static Date svr_date = null;                                                        // 서버시간
    public static String operation_time = "오전 09:00 ~ 익일 02:00";
    public static String open_sell_time = "7";                                                // 상품판매 시작시간
    public static String special_text = "";                                                 //단발성 이벤트 문구
    public static String special_theme_id = "";                                             //단발성 이벤트 테마 아이디
    public static String search_bg_text = "";                                               //단발성 이벤트 검색 힌트문구
    public static String special_text_q = "";                                                 //단발성 이벤트 문구
    public static String special_theme_id_q = "";                                             //단발성 이벤트 테마 아이디
    public static String search_bg_text_q = "";                                               //단발성 이벤트 검색 힌트문구
    public static String signupimgURL = null;                                               //  회원가입 이미지
    public static int maxDate = 181;                                                        //  달력최대일수
    public static String sign_pro_img = null;                                               // 프로모션 이미지

    //v3
    public final static String mainHome = domain + "/main";                                   // 메인 홈
    public final static String mainRecent = domain + "/recent";                         // 최근 본 상품
    public final static String hotel_detail = domain + "/stay";                            // 호텔상세
    public final static String search_before = domain + "/search/keyword/before";             // 검색 진입시
    public final static String search_auto = domain + "/search/keyword?query=";               // 자동완성
    public final static String booking_save_point = domain + "api/booking/check_discount/";   // 예약시 받을 포인트, 쿠폰 자주 호출
    public final static String search_stay_list = domain + "/stay?use_se=Y";                  // 검색결과 - 호텔
    public final static String search_activity_list = domain + "/activity?use_se=Y";                  // 검색결과 - 호텔
    public final static String like_list = domain + "/like/list";                             // 찜 - 호텔
    public final static String like_like = domain + "/like/like";                             // 찜 하기 - 호텔
    public final static String like_unlike = domain + "/like/unlike";                         // 찜 취소- 호텔
    public final static String ticketdetailUrl_v2 = domain + "/activity";                    //	티켓 상품 상세
    public final static String promotionUrl3 = domain + "/coupon/receive_instant/hotel";            //	쿠폰다운
    public final static String save_point = domain + "/api/booking/check_discount";            //	프로모션
    public final static String special_theme_list = domain + "/api/theme";                    // 테마 리스트
    public final static String review_show = domain + "/review";                          // 내 리뷰보기
    public final static String banner_list = domain + "/banner";                          // banner 전체보기
    public final static String hotdeal_list = domain + "/deallist";                       // 핫딜
    public final static String maketing_check = domain + "/marketing/change";             // 마켓팅 동의
    public final static String server_time = domain + "/servertime";                      // 서버시간
    public final static String maketing_agree = domain +"/api/agree";                      // 마켓팅 수신 동의 api
    public final static String maketing_agree_change = domain +"/api/agree/change";     // 약관 동의
    public static Boolean isRecent = false;                                           // 최근 상품이 없다 생겼을때 유무
    // 앱 종료시 초기화
    public static String sel_orderby = null;                                                // 검색가테고리
    public static String sel_category = null;
    public static String sel_rate = null;
    public static String sel_facility = null;
    public static String sel_useperson = null;
    public static String sel_max = "600000";
    public static String sel_min = "0";
    public static Boolean TabLogin = false;
    public static Boolean MYLOGIN = false;
    public static int sel_reserv = 0;
    public static int sel_fav = 0;
    public static boolean Mypage_Search = false;
    public static ArrayList<SearchResultItem> search_data = null;
    public static String[] selectList = null;
    public static boolean private_tag = false;

    public static String TAG = "HOTELNOW LOGGING";
    public static String kakaotalkimg = "http://d2gxin9b07oiov.cloudfront.net/web/kakaotalk.png";
    public final static String crash_log = domain + "/crash/android";

    public static int default_reserve_money = 5000;                                            //  적립금

    public static String lat = null;
    public static String lng = null;
}
