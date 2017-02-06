package pl.ttpsc.irrigation.errors;

public enum Error {
	ERROR0("ERROR-0"), ERROR1("ERROR-1"), ERROR2("ERROR-2");
	private final String description;
	private Error(final String description) {
		this.description = description;
	}
	public String getDescription() {
		return toString();
	}
	
	@Override
	public String toString() {
		return this.description;
	}
}
