package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.MailService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.UserDTO;
import org.kevoree.registry.web.rest.vm.KeyAndPasswordVM;
import org.kevoree.registry.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */

@RestController
@RequestMapping("/api")
public class AccountResource {

  private final Logger log = LoggerFactory.getLogger(AccountResource.class);

  @Inject
  private Environment env;

  @Inject
  private UserRepository userRepository;

  @Inject
  private NamespaceRepository namespaceRepository;

  @Inject
  private UserService userService;

  @Inject
  private MailService mailService;

  /**
   * POST  /register : register the user.
   *
   * @param managedUserVM the managed user View Model
   * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or e-mail is already in use
   */
  @PostMapping(path = "/register",
          produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
  @Timed
  public ResponseEntity<?> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

    HttpHeaders textPlainHeaders = new HttpHeaders();
    textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

    return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
            .orElseGet(() -> Optional.ofNullable(namespaceRepository.findOne(managedUserVM.getLogin().toLowerCase()))
                    .map(ns -> new ResponseEntity<>("namespace already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                    .orElseGet(() -> userRepository.findOneByEmail(managedUserVM.getEmail())
                            .map(user -> new ResponseEntity<>("e-mail address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                            .orElseGet(() -> {
                              User user = userService
                                      .createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                                              managedUserVM.getFirstName(), managedUserVM.getLastName(),
                                              managedUserVM.getEmail().toLowerCase(), managedUserVM.getLangKey());

                              mailService.sendActivationEmail(user);
                              return new ResponseEntity<>(HttpStatus.CREATED);
                            })
                    )
            );
  }

  /**
   * GET  /activate -> activate the registered user.
   */
  @RequestMapping(value = "/activate",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
    return Optional.ofNullable(userService.activateRegistration(key))
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  /**
   * GET  /authenticate -> check if the user is authenticated, and return its login.
   */
  @RequestMapping(value = "/authenticate",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public String isAuthenticated(HttpServletRequest request) {
    log.debug("REST request to check if the current user is authenticated");
    return request.getRemoteUser();
  }


  /**
   * GET  /account : get the current user.
   *
   * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
   */
  @GetMapping("/account")
  @Timed
  public ResponseEntity<UserDTO> getAccount() {
    return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  /**
   * POST  /account -> update the current user information.
   */
  @RequestMapping(value = "/account",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
    return userRepository
            .findOneByLogin(userDTO.getLogin())
            .filter(u -> u.getLogin().equals(SecurityUtils.getCurrentUserLogin()))
            .map(u -> {
              userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getLangKey());
              return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  /**
   * POST  /change_password -> changes the current user's password
   */
  @RequestMapping(value = "/account/change_password",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public ResponseEntity<?> changePassword(@RequestBody String password) {
    if (StringUtils.isEmpty(password)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    userService.changePassword(password);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * POST   /account/reset_password/init : Send an email to reset the password of the user
   *
   * @param mail the mail of the user
   * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad Request) if the email address is not registered
   */
  @PostMapping(path = "/account/reset_password/init",
          produces = MediaType.TEXT_PLAIN_VALUE)
  @Timed
  public ResponseEntity requestPasswordReset(@RequestBody String mail) {
    return userService.requestPasswordReset(mail)
            .map(user -> {
              mailService.sendPasswordResetMail(user);
              return new ResponseEntity<>("email was sent", HttpStatus.OK);
            }).orElse(new ResponseEntity<>("email address not registered", HttpStatus.BAD_REQUEST));
  }

  /**
   * POST   /account/reset_password/finish : Finish to reset the password of the user
   *
   * @param keyAndPassword the generated key and the new password
   * @return the ResponseEntity with status 200 (OK) if the password has been reset,
   * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
   */
  @PostMapping(path = "/account/reset_password/finish",
          produces = MediaType.TEXT_PLAIN_VALUE)
  @Timed
  public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
    if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
      return new ResponseEntity<>("incorrect password", HttpStatus.BAD_REQUEST);
    }
    return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  private boolean checkPasswordLength(String password) {
    return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
  }
}
