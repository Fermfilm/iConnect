iConnect

## 概要
このアプリは自分の外見情報や困った内容等をWiFi-Directを用いて周りのアプリ所有者に送信し，
受信した人が困った内容に承諾することで受信者の外見情報等を発信者に送信する事ができる助け合いアプリです．

## デモアプリインストール手順
コマンドプロンプトでapkのあるフォルダに移動し，以下を入力する．
adb install iConnect.apk

デモアプリ使用手順
Settingタブでデバイス名を決めて，Mainタブでプロフィールを設定し確認ボタンから周りのアプリ所持者に送信し，
近くのアプリ所持者は待っていたら通知を受信する．
しかし今は開発段階なので来ないこともあるが，メニューのDiscoverを一回押すことで受信できる．

## ディレクトリ構成
主要なファイルを簡単に説明する
・「libs」→「commons-io-2.4.jar」
ファイル削除やファイルコピーを簡略化してくれるライブラリファイル

以下「src」→「main」→「java」→「com/fermfilm/iConnect」より下のフォルダ・ファイルを参照
・「A_ResponseActivity.java」
助けてくれる人の外見情報等を表示するActivity

・「B_ResponseActivity.java」
助けて欲しい人の外見情報を表示するActivity

・「CallDialogActivity.java」
助けて欲しい人の外見情報をダイアログで表示するActivity

・「InOutState.java」
個別情報を保存・出力するClass

・「MainFragment.java」
Mainタブで動作するプロフィールや困った内容を設定するFragment

・「SettingFragment.java」
Settingタブで動作する通知設定や通知音，言語を設定するFragment

・「TabActivity.java」
メニューやReceiver，タブを管理するActivity

・「WifiService.java」
助けて欲しい人，助ける人によって情報を送信・受信の役割を変えるService

・「Entities」→「i_Message.java」
送信する，または受信した情報が入っているオブジェクトClass

・「i_AsyncTasks」
Server，Client毎の送受信非同期Thread

・「InitThreads」
Server，Clientのそれぞれ接続するためのThread

・「Receivers」
WiFi-Directの通信状態の変化を受け取るReceiver


## 環境構築手順ファイル
SETUP.txtを参照

