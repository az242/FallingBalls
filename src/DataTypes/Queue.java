package DataTypes;

public class Queue<T> {
	public class LinkList<T>{
		T val;
		LinkList<T> next;
		public LinkList(T data){
			val=data;
		}
	}
	LinkList<T> head;
	public boolean enQueue(T data){
		try{
			if(head==null){
				head = new LinkList<T>(data);
			}else{
				LinkList<T> temp = head;
				while(temp!=null){
					temp = temp.next;
				}
				temp.next = new LinkList<T>(data);
			}
			return true;
		}catch(Exception E){
			return false;
		}
	}
	public int size(){
		int tmp=0;
		LinkList<T> temp = head;
		while(temp!=null){
			temp = temp.next;
			tmp++;
		}
		return tmp;
	}
	public T deQueue(){
		LinkList<T> temp = head;
		head = head.next;
		return temp.val;
	}
}
