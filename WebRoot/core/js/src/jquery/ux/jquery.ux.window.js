(function($){
  var opts = {
    'modal': false,
    'draggable': {},
    'width': 300,
    'id': '',
    'tbar': [{
      id: 'close'
    }],
    'float': true,
    borderHeight: 40,
    modalCls: '',
    containment: 'parent',
    activeCls: 'window-active',
    'renderTo': 'body',
    'cmpCls': 'jq-window',
    'height': 300,
    'maskOpacity': '0.5',
    'closeAction': 'close',
    'src': '',
    'posCenter': true,
    'iframeSrc': '',
    'icon': '',
    layer: 'lower',
    'layout': 'autolayout',
    'layoutCfg': {},
    'plugins': [],
    'cls': '',
    'style': {},
    'listeners': {
      resize: $.noop
    }
  };
  
  T9.createComponent('Window', opts, T9.Panel.prototype, {
    initComponent: function() {
      if (this.modal) {
        this.layer = 'upper';
        this.createModalBg();
      }
      this.initContainer();
      //调用panel的initComponent函数
      T9.Panel.prototype.initComponent.call(this);
    },
    show: function() {
      T9.Panel.prototype.show.apply(this, arguments);
      this.modalBg && this.modalBg.show();
    },
    hide: function() {
      T9.Panel.prototype.hide.apply(this, arguments);
      this.modalBg && this.modalBg.hide();
    },
    close: function() {
      T9.Panel.prototype.close.apply(this, arguments);
      //close调用的是hide和destroy 不需要hidemask
      //this.modalBg && this.modalBg.hide();
    },
    initContainer: function() {
      this.el.addClass(this.cmpCls + '-container');
      this.el.css({
        'width': this.width,
        'z-index': T9.topZIndex(this.layer)
      });
      
      var t = this;

      this.el.hide();
      this.status.hidden = true;
      
      //添加控件显示隐藏销毁时对应的遮罩层处理的事件
      if (t.modalBg) {
        T9.Event.add(this, 'bind', 'destroy', 'before', function(e, t) {
          t.modalBg.remove();
        });
        
        $(window).resize(function() {
          t.modalBg.css({
            'width': (document.documentElement || document.body).scrollWidth,
            'height': (document.documentElement || document.body).scrollHeight
          });
        });
      }
    },
    createModalBg: function() {
      this.modalBg = $('<div class="' + this.cmpCls + '-mask"></div>');
      $('body').append(this.modalBg);
      var CSS = {
        'width': (document.documentElement || document.body).scrollWidth,
        'height': (document.documentElement || document.body).scrollHeight,
        'top': '0px',
        'z-index': T9.topZIndex(this.layer),
        'left': '0px',
        'display': 'none',
        'opacity': this.maskOpacity,
        'filter': 'alpha(opacity=' + this.maskOpacity * 100 + ');WIDTH: 100%'
      };
      this.modalBg.css(CSS);
      this.modalBg.addClass(this.modalCls);
      if (this.modalStyle) {
        this.modalBg.css(this.modalStyle);
      }
    }
  });
  
  var windowMrgOpts = {
    tray: null,
    container: null,
    items: [],
    activeWindow: null,
    activeBtn: null
  };
  
  /**
   * 窗口管理器
   */
  T9.createComponent('WindowMgr', windowMrgOpts, {
    initialize: function() {
      var self = this;
      
      $.each(this.items, function(i, e) {
        self.addTrayBtn(e);
      });
    },
    addTrayBtn: function(win) {
      var self = this;
      if (this.tray && !win.trayBtn) {
        var btn = new T9.Button({
          status: {
            isPressed: !win.status.hidden
          },
          normalCls:'btn-icon',
          activeCls: 'btn-icon-active',
          icon: win.icon,
          text: win.title,
          toggle: true,
          title: win.title,
          toggleHandler: [function(e, t, callback) {
            win.show('normal', callback)
            win.active();
          },function(e, t, callback){
            win.hide('normal', callback);
          }]
        });
        
        //删除Tray中的btn引用
        T9.Event.add(btn, 'bind', 'destroy', 'before', function(e, t) {
          self.tray.items = $.grep(self.items, function(n, i){
            return n.id != t.id;
          });
        });
        
        win.trayBtn = btn;
        this.tray.addItems(btn);
        
      }
      
      function doActive(e, t) {
        //第一次创建窗口时,设置这个窗口的托盘图标为激活状态

        if (!self.activeWindow) {
          self.activeWindow = t;
          t.trayBtn.setStatus('active', true);
        }
        else if (self.activeWindow.id != t.id) {
          self.activeWindow.trayBtn && self.activeWindow.trayBtn.setStatus('default', true);
          self.activeWindow.deactive();
          self.activeWindow = t;
          t.trayBtn.setStatus('active', true);
        }
      }
      
      function doHide(e, t) {
        var id = '', max = 0;
        $.each(self.items, function(i, e){
          if(!e.status.hidden && e.id != t.id) {
            if ((e.zIndex || 0) > max) {
              max = e.zIndex || 0;
              id = e.id;
            }
          }
        });
        if (id) {
          T9.getCmp(id).active();
        }
        else {
          t.trayBtn && t.trayBtn.setStatus('default', true);
        }
      }
      
      T9.Event.add(win, 'bind', 'active', 'after', doActive);
      T9.Event.add(win, 'bind', 'hide', 'after', doHide);
      //删除WindowMgr中的window引用
      T9.Event.add(win, 'bind', 'destroy', 'before', function(e, t) {
        self.items = $.grep(self.items, function(n, i){
          return n.id != t.id;
        });
        t.trayBtn && t.trayBtn.destroy();
      });
    },
    addItems: function(windows) {
      var self = this;
      if (windows instanceof Array) {
        $.each(windows, function(i, e) {
          self.items.push(e);
          self.addTrayBtn(e);
        });
      }
      else {
        this.items.push(windows);
        this.addTrayBtn(windows);
      }
    },
    createWindow: function(cfg) {
      var win = T9.getCmp(cfg && cfg.id);
      if (!win) {
        win = new T9.Window(cfg);
        win.el.addClass(this.cls);
        this.addItems(win);
      }
      win.active();
    },
    createPanel: function(cfg) {
      if (!T9.isCmp(cfg.parentCmp)) {
        return;
      }
      var panel = new T9.Panel(cfg);
      this.addItems(panel);
      panel.active();
    },
    hideAll: function() {
      $.each(this.items, function(i, e) {
        e.hide();
      });
    },
    removeAll: function() {
      $.each(this.items, function(i, e) {
        T9.isCmp(e) && e.destroy();
      });
    },
    showAll: function() {
      $.each(this.items, function(i, e) {
        e.show();
      });
    },
    getActive: function() {
      return this.activeWindow;
    }
  });
})(jQuery);
