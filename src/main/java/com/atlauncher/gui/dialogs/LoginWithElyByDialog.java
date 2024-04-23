package com.atlauncher.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mini2Dx.gettext.GetText;

import com.atlauncher.App;
import com.atlauncher.constants.Constants;
import com.atlauncher.data.AbstractAccount;
import com.atlauncher.data.ElybyAccount;
import com.atlauncher.data.elyby.OauthTokenResponse;
import com.atlauncher.data.elyby.Profile;
import com.atlauncher.gui.panels.LoadingPanel;
import com.atlauncher.managers.AccountManager;
import com.atlauncher.managers.DialogManager;
import com.atlauncher.managers.LogManager;
import com.atlauncher.utils.ElyByAuthAPI;
import com.atlauncher.utils.OS;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.VirtualHost;


@SuppressWarnings("serial")
public class LoginWithElyByDialog extends JDialog {
    private static final HTTPServer server = new HTTPServer(Constants.ELYBY_LOGIN_REDIRECT_PORT);
    private static final VirtualHost host = server.getVirtualHost(null);

    public ElybyAccount account = null;

    public LoginWithElyByDialog() {
        this(null);
    }

    public LoginWithElyByDialog(ElybyAccount account) {
        super(App.launcher.getParent(), GetText.tr("Login with Ely.by"), ModalityType.DOCUMENT_MODAL);

        this.account = account;
        this.setMinimumSize(new Dimension(400, 400));
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.add(new LoadingPanel(GetText.tr("Browser opened to complete the login process")), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel linkPanel = new JPanel(new FlowLayout());

        JTextField linkTextField = new JTextField(Constants.ELYBY_LOGIN_URL);
        linkTextField.setPreferredSize(new Dimension(300, 23));
        linkPanel.add(linkTextField, BorderLayout.SOUTH);

        JButton linkCopyButton = new JButton("Copy");
        linkCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkTextField.selectAll();
                OS.copyToClipboard(Constants.ELYBY_LOGIN_URL);
            }
        });
        linkPanel.add(linkCopyButton);

        JLabel infoLabel = new JLabel("<html>"
                + GetText.tr("If your browser hasn't opened, please manually open the below link in your browser")
                + "</html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        bottomPanel.add(infoLabel, BorderLayout.CENTER);
        bottomPanel.add(linkPanel, BorderLayout.SOUTH);

        this.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(false);
        dispose();

        OS.openWebBrowser(Constants.ELYBY_LOGIN_URL);

        try {
            startServer();
        } catch (IOException e) {
            LogManager.logStackTrace("Error starting web server for Ely.by login", e);

            close();
        }

        this.setLocationRelativeTo(App.launcher.getParent());
        this.setVisible(true);
    }

    private void close() {
        server.stop();
        setVisible(false);
        dispose();
    }

    private void startServer() throws IOException {
        host.addContext("/", (req, res) -> {
            if (req.getParams().containsKey("error")) {
                res.getHeaders().add("Content-Type", "text/plain");
                res.send(500, GetText.tr("Error logging in. Check console for more information"));
                LogManager.error("Error logging into Ely.by account: " +
                        URLDecoder.decode(req.getParams().get("error_message"), StandardCharsets.UTF_8.toString()));
                close();
                return 0;
            }

            if (!req.getParams().containsKey("code")) {
                res.getHeaders().add("Content-Type", "text/plain");
                res.send(400, GetText.tr("Code is missing"));
                close();
                return 0;
            }

            try {
                login(req.getParams().get("code"));
            } catch (Exception e) {
                LogManager.logStackTrace("Error acquiring accessToken", e);
                res.getHeaders().add("Content-Type", "text/html");
                res.send(500, GetText.tr("Error logging in. Check console for more information"));
                close();
                return 0;
            }

            res.getHeaders().add("Content-Type", "text/plain");
            // #. {0} is the name of the launcher (ATLauncher)
            res.send(200, GetText.tr("Login complete. You can now close this window and go back to {0}",
                    Constants.LAUNCHER_NAME));
            close();
            return 0;
        }, "GET");

        server.start();
    }

    private void addAccount(OauthTokenResponse oauthTokenResponse, Profile profile) throws Exception {
        if (account != null || AccountManager.isAccountByName(profile.username)) {
            AbstractAccount abstractAccount = AccountManager.getAccountByName(profile.username);
            if (!(abstractAccount instanceof ElybyAccount)) {
                DialogManager.okDialog().setTitle(GetText.tr("Account Not Added"))
                        .setContent(
                                GetText.tr("Account with this username already exists, please remove that before adding this one."))
                        .setType(DialogManager.ERROR).show();
                return;          
            }

            ElybyAccount account = (ElybyAccount) abstractAccount;

            // if forced to relogin, then make sure they logged into correct account
            if (this.account != null && !account.username.equals(this.account.username)) {
                DialogManager.okDialog().setTitle(GetText.tr("Incorrect account"))
                        .setContent(
                                GetText.tr("Logged into incorrect account. Please login again on the Accounts tab."))
                        .setType(DialogManager.ERROR).show();
                return;
            }

            account.update(oauthTokenResponse, profile);
            AccountManager.saveAccounts();
        } else {
            ElybyAccount account = new ElybyAccount(oauthTokenResponse, profile);

            AccountManager.addAccount(account);
            this.account = account;
        }
    }

    private void login(String authCode) throws Exception {
         OauthTokenResponse oauthTokenResponse = ElyByAuthAPI.getAccessToken(authCode);

        Profile profile = ElyByAuthAPI.getProfile(oauthTokenResponse.accessToken);
        if (profile == null) {
            throw new Exception("Failed to get Minecraft profile");
        }

        addAccount(oauthTokenResponse, profile);
    }
}
