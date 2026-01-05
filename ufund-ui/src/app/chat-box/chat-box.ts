import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { User } from '../user';
import { ChatService } from '../chat-service';
import { Chat } from '../chat';
import { ChatPersonality } from '../ChatPersonality';

@Component({
  selector: 'app-chat-box',
  standalone: false,
  templateUrl: './chat-box.html',
  styleUrl: './chat-box.css',
})
export class ChatBox implements OnInit, OnDestroy {
  @Input() currentUser!: User;
  @Output() closeEvent = new EventEmitter<void>();
  chats: Chat[] = [];
  personalityType!: ChatPersonality;
  responseChat: Chat = {
    username: 'You',
    message: '',
  };
  personalities: ChatPersonality[] = [];

  constructor(private chatService: ChatService) {}

  ngOnInit() {
    this.chatService.getPersonalities().subscribe((data) => {
      data.forEach((element) => {
        this.personalities.push(element);
      });
    });
  }

  onClose() {
    this.closeEvent.emit();
  }

  onSelect(personality: ChatPersonality) {
    let defaultPersonality = document.getElementById('default-personality') as HTMLOptionElement;
    defaultPersonality.disabled = true;
    this.chatService.chatExists(this.currentUser.id).subscribe({
      next: (exists) => {
        if (exists) {
          this.chatService.deleteChat(this.currentUser.id).subscribe();
        }
      },
      complete: () => {
        if (personality != null) {
          this.personalityType = personality;
          this.chatService.registerChat(this.currentUser.id, personality).subscribe(() => {
            this.personalityType = personality;
            let chat: Chat = {
              username: this.personalityType.name,
              message: 'Hello, ' + this.currentUser.userName + '! What can I help you with?',
            };
            this.chats.push(chat);
          });
        }
      },
    });
  }

  sendChat(message: string) {
    if (message != '' && this.personalityType != null) {
      this.chats.push(this.responseChat);
      this.chats = structuredClone(this.chats);
      this.chatService.sendChat(this.currentUser.id, message).subscribe((data) => {
        let chat: Chat = {
          username: this.personalityType.name,
          message: data,
        };
        this.chats.push(chat);
      });
      this.responseChat.message = '';
    }
  }

  ngOnDestroy() {
    this.chatService.deleteChat(this.currentUser.id).subscribe();
    this.chats.length = 0;
  }
}
