(function($) {
  var opts = {
    el: $('<div></div>'),
    items: [],
    draggable: {
      containment: 'parent'
    },
    resizable: {},
    position: 'center',
    droppableCls: 'free-droppable',
    cmpCls: 'jq-freelayout',
    droppableCmps: []
  };
  
  T9.createComponent.call(T9.layouts, "FreeLayout", opts, T9.layouts.AutoLayout.prototype, {
    initComponent: function() {
      this.el = $(this.el);
      if (this.droppable) {
        this.el.data('cmp', this.owner);
        this.el.addClass(this.droppableCls);
      }
      //this.doDraggable();
      //this.doResizable();
      var self = this;
      $.each(this.items, function(i, e) {
        self.items[i] = e = self.renderItem(e, i);
        self.doItem(e);
      });
      this.doDroppable(this.owner);
    },
    doDraggable: function(draggable) {
      $.extend(true, this.draggable, draggable);
      var self = this;
      if (!this.draggable) {
        return;
      }
      if (this.items instanceof Array && this.items.length) {
        $.each(this.items, function(i, e) {
          self.doItemDrag(e);
        });
      }
      else {
        this.el.children().draggable(this.draggable);
      }
    },
    doResizable: function(resizable) {
      $.extend(true, this.resizable, resizable);
      var self = this;
      if (!this.resizable) {
        return;
      }
      if (this.items instanceof Array && this.items.length) {
        $.each(this.items, function(i, e) {
          self.doItemResize(e);
        });
      }
      else {
        this.el.children().resizable(this.resizable);
      }
    },
    doItemResize: function(e) {
      if (T9.isCmp(e)) {
        if (!e.status.resizable) {
          e.resizable = {};
          $.extend(true, e.resizable, this.resizable);
          e.doResizable && e.doResizable();
        }
      }
    },
    doItemDrag: function(e) {
      if (T9.isCmp(e)) {
        if (!e.draggable) {
          e.draggable = {};
          $.extend(true, e.draggable, this.draggable);
          e.doDraggable && e.doDraggable();
        }
        else if (e.drag){
          e.drag.option('containment', 'parent');
        }
        
        if (!e.left && !e.top) {
          if (self.position == 'center') {
            e.setPosCenter();
          }
        }
        
        if (!e.status.clickActive) {
          e.clickActive && e.clickActive();
        }
        e.setStyle({
          'left': e.left,
          'top': e.top
        });
      }
    },
    doItem: function(e) {
      this.doItemDrag(e);
      this.doItemResize(e);
      if (T9.isCmp(e)) {
        e.setStyle({
          'position': 'absolute'
        });
      }
    }
  });
})(jQuery);