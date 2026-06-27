package nl.guidobreuer.webodesolver.dto;

public class OptionsDTO {

	private final boolean maintainAspectRatio = false;
	private final ScalesDTO scales;
	
	public OptionsDTO(String xlabel, String ylabel) {
		scales = new ScalesDTO(xlabel, ylabel);
	}
	
	public boolean isMaintainAspectRatio() {
		return maintainAspectRatio;
	}

	public ScalesDTO getScales() {
		return scales;
	}
	
	
	
	
	public static class ScalesDTO {
		
		private final Axis x;
		private final Axis y;
		
		public Axis getX() {
			return x;
		}

		public Axis getY() {
			return y;
		}

		ScalesDTO(String xlabel, String ylabel) {
			x = new Axis(xlabel);
			y = new Axis(ylabel);
		}
		
		
		
		
		
		
		public static class Axis {
			
			private final Title title;
			
			public Title getTitle() {
				return title;
			}

			public Axis(String text) {
				title = new Title(text);
			}
			
			
			
			
			
			
			
			
			
			
			
			public static class Title {
				
				private final boolean display = true;
				private final String text;
				
				public Title(String text) {
					this.text = text;
				}
				
				public boolean isDisplay() {
					return display;
				}
				public String getText() {
					return text;
				}


			}
			
			
		}

		
	}

	
}
